package com.yauhenl.poe.domain;

import org.bson.Document;

import java.util.List;

public class Stash {

    public static Boolean isPublic(Document stash) {
        return stash.getBoolean("public");
    }

    @SuppressWarnings("unchecked")
    public static List<Document> getItems(Document stash) {
        return stash.get("items", List.class);
    }

    public static void setItems(Document stash, List<Document> items) {
        stash.put("items", items);
    }

    public static String getId(Document stash) {
        return stash.getString("id");
    }

    public static Boolean isVerified(Document item) {
        return item.getBoolean("verified");
    }

    public static String getAccountName(Document stash) {
        return stash.getString("accountName");
    }

    public static String getLastCharacterName(Document stash) {
        return stash.getString("lastCharacterName");
    }
}
