package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.exists
import khttp.get
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ApiService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "apiData") {
    private val log: Logger = LoggerFactory.getLogger(ApiService::class.java)

    @Value("\${api.baseUrl}")
    private val baseUrl: String? = null

    @Value("\${api.publicStashTabs}")
    private val publicStashTabs: String? = null

    fun getPublicStashTabs(): List<Document> {
        var url = baseUrl + publicStashTabs
        val nextChangeIdData = findNextChangeId()
        val nextChangeId = getNextChangeId(nextChangeIdData)
        if ("" != nextChangeId) {
            url += "/?id=" + nextChangeId
        }
        val data = Document.parse(get(url).text)
        log.info(getNextChangeId(data))
        setNextChangeId(nextChangeIdData, getNextChangeId(data))
        replaceOne(nextChangeIdData)
        return getStashes(data) as List<Document>
    }

    fun findNextChangeId() = mongoCollection.find(exists("next_change_id")).first()

    fun getNextChangeId(data: Document) = data.getString("next_change_id")
    fun setNextChangeId(data: Document, value: String) = data.put("next_change_id", value)

    fun getStashes(data: Document) = data.get("stashes", List::class.java)

    init {
        if (mongoCollection.count().toInt() == 0) {
            mongoCollection.insertOne(Document("next_change_id", ""))
        }
    }
}