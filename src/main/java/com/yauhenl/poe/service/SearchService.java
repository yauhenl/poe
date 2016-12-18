package com.yauhenl.poe.service;

import org.bson.Document;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.yauhenl.poe.domain.Stash.*;

@Service
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private static final String stashType = "stash";

    @Value("${spring.data.elasticsearch.indexAlias}")
    private String indexAliasName;

    @Autowired
    private Client elasticSearchClient;

    @Autowired
    private StashService stashService;

    @Async
    public void reindexProducts() {
        logger.info(elasticSearchClient.toString());
        String newIndexName = String.format("newproduct-%d", System.currentTimeMillis());
        createIndex(newIndexName);
        processStashes(newIndexName);
        createOrSwitchAlias(indexAliasName, newIndexName);
    }

    private void createIndex(String indexName) {
        elasticSearchClient.admin().indices().prepareCreate(indexName).execute().actionGet();
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
        stashService.findIds().parallelStream().forEach(id -> {
            long c = counter.addAndGet(1);
            Document stash = stashService.findById(getId(id));
            if (stash != null) {
                elasticSearchClient.prepareIndex(indexName, stashType, getId(stash)).setSource(processStash(stash)).execute().actionGet(5, TimeUnit.SECONDS);
            }
            if (c % 1000 == 0) {
                logger.info("index {} - {} processed", indexName, c);
            }
        });
    }

    private Map<String, Object> processStash(Document stash) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", getId(stash));
        result.put("accountName", getAccountName(stash));
        result.put("lastCharacterName", getLastCharacterName(stash));
        return result;
    }
}
