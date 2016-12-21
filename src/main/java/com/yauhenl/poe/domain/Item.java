package com.yauhenl.poe.domain;

import org.bson.Document;

import java.util.List;

public class Item {

    @SuppressWarnings("unchecked")
    public static List<Document> getSocketedItems(Document item) {
        return item.get("socketedItems", List.class);
    }
}
