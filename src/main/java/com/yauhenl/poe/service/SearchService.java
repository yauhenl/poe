package com.yauhenl.poe.service;

import org.bson.Document;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static com.yauhenl.poe.domain.Stash.getId;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private static final String stashType = "stash";

    @Value("${spring.data.elasticsearch.indexAlias}")
    private String indexAliasName;

    @Value("classpath:es_index_source.json")
    private Resource indexMapping;

    @Autowired
    private Client elasticSearchClient;

    @Autowired
    private StashService stashService;

    @Async
    public void reindexProducts() throws IOException {
        logger.info(elasticSearchClient.toString());
        String newIndexName = String.format("newproduct-%d", System.currentTimeMillis());
        createIndex(newIndexName);
        processStashes(newIndexName);
        createOrSwitchAlias(indexAliasName, newIndexName);
    }

    private void createIndex(String indexName) throws IOException {
        elasticSearchClient.admin().indices().prepareCreate(indexName).setSource(StreamUtils.copyToByteArray(indexMapping.getInputStream())).execute().actionGet();
    }

    private void createOrSwitchAlias(String aliasName, String indexName) {
        final boolean aliasExists = elasticSearchClient.admin().indices().prepareAliasesExist(aliasName).execute().actionGet().isExists();
        final IndicesAliasesRequestBuilder aliases = elasticSearchClient.admin().indices().prepareAliases();
        if (aliasExists) {
            elasticSearchClient.admin().indices().prepareGetAliases(aliasName)
                    .execute().actionGet().getAliases()
                    .forEach(cursor -> {
                        final String key = cursor.key;
                        final List<AliasMetaData> value = cursor.value;
                        value.forEach(aliasMetaData -> aliases.removeAlias(key, aliasMetaData.alias()));
                    });
            aliases.addAlias(indexName, aliasName);
        } else {
            aliases.addAlias(indexName, aliasName);
        }
        aliases.execute().actionGet();
    }

    private void processStashes(String indexName) {
        final AtomicLong counter = new AtomicLong(0);
        stashService.find().forEach((Consumer<Document>) stash -> {
            long c = counter.addAndGet(1);
            try {
                elasticSearchClient.prepareIndex(indexName, stashType, getId(stash)).setSource(processStash(stash)).execute().actionGet(5, TimeUnit.SECONDS);
            } catch (IllegalArgumentException e) {
                logger.error("Mapping error " + stash.toJson(), e);
            }
            if (c % 1000 == 0) {
                logger.info("index {} - {} processed", indexName, c);
            }
        });
    }

    private Document processStash(Document stash) {
        stash.remove("_id");
        return stash;
    }
}
