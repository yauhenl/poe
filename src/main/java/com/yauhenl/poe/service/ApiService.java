package com.yauhenl.poe.service;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static khttp.KHttp.get;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Value("${api.publicStashTabs}")
    private String publicStashTabs;

    public Document getPublicStashTabs(String nextChangeId) {
        String url = publicStashTabs + nextChangeId;
        String getResult = get(url, Collections.singletonMap("Content-Encoding", "gzip")).getText();
        Document data = Document.parse(getResult);
        if (getError(data) != null) {
            logger.error("get error", data.toJson());
            return null;
        }
        return data;
    }

    public Document getError(Document data) {
        return data.get("error", Document.class);
    }
}
