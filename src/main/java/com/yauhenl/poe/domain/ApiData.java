package com.yauhenl.poe.domain;

import org.bson.Document;

import java.util.List;

public class ApiData {
    public static String getNextChangeId(Document data) {
        return data.getString("next_change_id");
    }

    public static void setNextChangeId(Document data, String value) {
        data.put("next_change_id", value);
    }

    public static Document getError(Document data) {
        return data.get("error", Document.class);
    }

    @SuppressWarnings("unchecked")
    public static List<Document> getStashes(Document data) {
        return data.get("stashes", List.class);
    }
}
