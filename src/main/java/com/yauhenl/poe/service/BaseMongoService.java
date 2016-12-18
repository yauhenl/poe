package com.yauhenl.poe.service;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public abstract class BaseMongoService {

    protected MongoCollection<Document> mongoCollection;

    public BaseMongoService(MongoDatabase mongoDatabase, String mongoCollectionName) {
        mongoCollection = mongoDatabase.getCollection(mongoCollectionName);
        if (mongoCollection != null && mongoCollection.count() == 0) {
            initCollection();
        }
    }

    public void initCollection() {
    }

    public void drop() {
        mongoCollection.drop();
    }

    public void insertOne(Document document) {
        mongoCollection.insertOne(document);
    }

    public void insertMany(List<Document> documents) {
        mongoCollection.insertMany(documents);
    }

    public void replaceOne(Document document) {
        mongoCollection.replaceOne(eq("_id", document.get("_id")), document, new UpdateOptions().upsert(true));
    }

    public void deleteOne(Document document) {
        mongoCollection.deleteOne(document);
    }

    public void truncate() {
        mongoCollection.deleteMany(new Document());
    }

    public Object getMongoId(Document doc) {
        return doc.get("_id");
    }

    public void deleteByIds(List<Object> ids) {
        mongoCollection.deleteMany(in("_id", ids));
    }

    public Document findById(Object id) {
        return mongoCollection.find(eq("_id", id)).first();
    }

    public Document findFirstByProperty(String key, Object value) {
        return mongoCollection.find(eq(key, value)).first();
    }

    public FindIterable<Document> find() {
        return mongoCollection.find();
    }

    public long count() {
        return mongoCollection.count();
    }

    public Document findFirst() {
        return mongoCollection.find().first();
    }

    public void updateOneFieldById(String id, String fieldName, Object value) {
        mongoCollection.updateOne(eq("_id", new ObjectId(id)), new Document("$set", new Document(fieldName, value)));
    }

    protected <T> List<T> consumeToList(FindIterable<T> input) {
        List<T> result = new ArrayList<>();
        input.forEach((Consumer<T>) result::add);
        return result;
    }
}
