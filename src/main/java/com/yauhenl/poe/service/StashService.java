package com.yauhenl.poe.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.bson.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static khttp.KHttp.get;

@Service
public class StashService {

    @Value("${url.getStashItems}")
    private String getStashItemsUrl;

    public void getStashes() {
        Map<String, String> params = new HashMap<>();
        params.put("accountName", "yauhel");
        params.put("tabIndex", "6");
        params.put("league", "Delve");
        params.put("tabs", "0");
        Map<String, String> cookies = new HashMap<>();
        cookies.put("POESESSID", "ec9bcdf27bcb455854170b8cb0f0040a");
        String getResult = get(getStashItemsUrl, Collections.singletonMap("Content-Encoding", "gzip"), params, null, null, null, cookies).getText();
        Document data = Document.parse(getResult);
        System.out.println(data);
    }
}
