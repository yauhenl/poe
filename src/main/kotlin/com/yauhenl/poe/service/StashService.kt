package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.UpdateOptions
import com.yauhenl.poe.domain.Stash.getId
import com.yauhenl.poe.domain.Stash.getItems
import com.yauhenl.poe.domain.Stash.isPublic
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
open class StashService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "stash") {
    private val log: Logger = LoggerFactory.getLogger(StashService::class.java)

    @Autowired
    private val apiService: ApiService? = null

    override fun replaceOne(document: Document) = mongoCollection.replaceOne(Filters.eq("id", getId(document)), document, UpdateOptions().upsert(true))

    @Scheduled(fixedRate = 5000)
//    @Async
    fun updateStashes() {
        apiService?.getPublicStashTabs()?.forEach { it ->
            val stash = findFirstByProperty("id", getId(it))
            if (stash == null) {
                if (isPublic(it) && getItems(it).isNotEmpty()) {
                    insertOne(it)
                }
            } else {
                if (!isPublic(it) || getItems(it).isEmpty()) {
                    deleteOne(stash)
                } else {
                    replaceOne(it)
                }
            }
        }
        log.info("============================================")
    }

    init {
        if (mongoCollection.count().toInt() == 0) {
            mongoCollection.createIndex(Document("id", 1), IndexOptions().unique(true))
        }
    }
}