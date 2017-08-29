package com.yauhenl.poe.service;

import com.mongodb.client.MongoDatabase;
import com.yauhenl.poe.domain.JobStatus;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

import static com.mongodb.client.model.Sorts.descending;

@Service
public class PublicStashTabsService extends BaseMongoService {
    private static final Logger logger = LoggerFactory.getLogger(PublicStashTabsService.class);

    private static final String collectionName = "publicStashTabs";

    @Autowired
    public PublicStashTabsService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    @Autowired
    private ApiService apiService;

    @Async
    public Future<JobStatus> writeNext() {
        Document last = mongoCollection.find().sort(descending("_id")).limit(1).first();
        String nextChangeId = last == null ? "0" : getNextChangeId(last);
        Document publicStashTabs = apiService.getPublicStashTabs(nextChangeId);
        if (publicStashTabs != null && !nextChangeId.equals(getNextChangeId(publicStashTabs))) {
            insertOne(publicStashTabs);
            logger.info(getNextChangeId(publicStashTabs));
        }
        return new AsyncResult<>(JobStatus.done);
    }

    public String getNextChangeId(Document document) {
        return document.getString("next_change_id");
    }
}
