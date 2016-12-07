package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.exists
import khttp.get
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service


@Service
class ApiService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "apiData") {
    @Value("\${api.baseUrl}")
    private val baseUrl: String? = null

    @Value("\${api.publicStashTabs}")
    private val publicStashTabs: String? = null

    fun getPublicStashTabs(): List<Document> {
        var url = baseUrl + publicStashTabs
        val nextChangeIdData = getNextChangeId()
        if (nextChangeIdData != null) {
            val nextChangeId = getNextChangeId(nextChangeIdData)
            if (nextChangeId != null && "0" != nextChangeId) {
                url += "/?id=" + nextChangeId
            }
        }
        val data = Document.parse(get(url).text)
        setNextChangeId(nextChangeIdData, getNextChangeId(data))
        replaceOne(nextChangeIdData)
        return getStashes(data) as List<Document>
    }

    fun getNextChangeId(): String? {
        val nextChangeId = mongoCollection.find(exists("next_change_id")).first()
        if (nextChangeId != null) {
            return getNextChangeId(nextChangeId)
        }
        return null
    }

    fun getNextChangeId(data: Document) = data.getString("next_change_id")
    fun setNextChangeId(data: Document, value: String) = data.put("next_change_id", value)

    fun getStashes(data: Document) = data.get("stashes", List::class.java)
}