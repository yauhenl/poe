package com.yauhenl.poe.service;

import static com.mongodb.client.model.Filters.eq;
import static com.yauhenl.poe.domain.Stash.*;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.UpdateOptions;

@Service
public class StashService extends BaseMongoService {
    private static final String collectionName = "stash";
    private static final Logger logger = LoggerFactory.getLogger(StashService.class);

    @Autowired
    private ApiService apiService;

    @Autowired
    public StashService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    @Override
    public void initCollection() {
        mongoCollection.createIndex(new Document("id", 1), new IndexOptions().unique(true));
    }

    @Override
    public void replaceOne(Document document) {
        mongoCollection.replaceOne(eq("id", getId(document)), document, new UpdateOptions().upsert(true));
    }

    @Async
    public void updateStashes() {
        apiService.getPublicStashTabs().forEach(it -> {
            Document stash = findFirstByProperty("id", getId(it));
            if (stash == null) {
                if (isPublic(it) && !getItems(it).isEmpty()) {
                    insertOne(it);
                }
            } else {
                if (!isPublic(it) || getItems(it).isEmpty()) {
                    deleteOne(stash);
                } else {
                    replaceOne(it);
                }
            }
        });
        logger.info("============================================");
    }
}
