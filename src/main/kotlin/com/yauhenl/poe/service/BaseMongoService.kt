package com.yauhenl.poe.service

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import org.bson.Document


abstract class BaseMongoService(mongoDatabase: MongoDatabase, mongoCollectionName: String) {
    protected var mongoCollection: MongoCollection<Document>

    fun initCollection() {
    }

    fun drop() = mongoCollection.drop()

    fun insertOne(document: Document) = mongoCollection.insertOne(document)

    fun replaceOne(document: Document) = mongoCollection.replaceOne(eq("_id", document["_id"]), document, UpdateOptions().upsert(true))

    fun deleteOne(document: Document) = mongoCollection.deleteOne(document)

    fun truncate() = mongoCollection.deleteMany(Document())

    fun deleteByIds(ids: List<Any>) = mongoCollection.deleteMany(`in`("_id", ids))

    fun getMongoId(doc: Document) = doc["_id"]

    fun findById(id: Any) = mongoCollection.find(eq("_id", id)).first()

    fun findFirstByProperty(key: String, value: Any) = mongoCollection.find(eq(key, value)).first()

    fun count() = mongoCollection.count()

    fun findFirst() = mongoCollection.find().first()

    init {
        mongoCollection = mongoDatabase.getCollection(mongoCollectionName)
        if (mongoCollection.count().equals(0)) {
            initCollection()
        }
    }
}