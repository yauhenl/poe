package com.yauhenl.poe.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
public class StashService extends BaseMongoService {
    private static final String collectionName = "stashes";

    @Autowired
    private PublicStashTabsService publicStashTabsService;

    public StashService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    @Override
    public void initCollection() {
        mongoCollection.createIndex(new Document("id", 1), new IndexOptions().unique(true));
    }

    public void parseData(int pageNumber, int pageSize) {
        FindIterable<Document> documents = publicStashTabsService.find().skip(pageNumber * pageSize).limit(pageSize);
        documents.forEach((Consumer<? super Document>) publicStashTab -> getStashes(publicStashTab).stream().filter(this::isPublic).forEach(stash -> {
            Document existStash = findByStashId(getStashId(stash));
            if (existStash != null) {
                stash.put("_id", existStash.get("_id"));
                replaceOne(stash);
            } else {
                insertOne(stash);
            }
        }));
    }

    public Document findByStashId(String stashId) {
        return findFirstByProperty("id", stashId);
    }

    public List<Document> getStashes(Document document) {
        return document.get("stashes", List.class);
    }

    public Boolean isPublic(Document document) {
        return document.getBoolean("public");
    }

    public List<Document> getItems(Document document) {
        return document.get("items", List.class);
    }

    public String getStashId(Document document) {
        return document.getString("id");
    }
}
