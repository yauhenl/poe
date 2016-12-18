package com.yauhenl.poe.service;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.mongodb.client.model.Filters.exists;
import static com.yauhenl.poe.domain.ApiData.*;
import static khttp.KHttp.get;

@Service
public class ApiService extends BaseMongoService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
    private static final String collectionName = "apiData";

    @Value("${api.baseUrl}")
    private String baseUrl;

    @Value("${api.publicStashTabs}")
    private String publicStashTabs;

    @Autowired
    public ApiService(MongoDatabase mongoDatabase) {
        super(mongoDatabase, collectionName);
    }

    @Override
    public void initCollection() {
        mongoCollection.insertOne(new Document("next_change_id", ""));
    }

    public List<Document> getPublicStashTabs() {
        String url = baseUrl + publicStashTabs;
        Document nextChangeIdData = findNextChangeId();
        String nextChangeId = getNextChangeId(nextChangeIdData);
        if (!"".equals(nextChangeId)) {
            url += "/?id=" + nextChangeId;
        }
        String getResult = get(url, Collections.singletonMap("Content-Encoding", "gzip")).getText();
        Document data = Document.parse(getResult);
        if (getError(data) != null) {
            logger.error("get error", data.toJson());
            return Collections.singletonList(new Document());
        }
        logger.info(getNextChangeId(data));
        setNextChangeId(nextChangeIdData, getNextChangeId(data));
        replaceOne(nextChangeIdData);
        return getStashes(data);
    }

    private Document findNextChangeId() {
        return mongoCollection.find(exists("next_change_id")).first();
    }
}
