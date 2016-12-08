package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*

@Service
class StashService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "stash") {
    private val log: Logger = LoggerFactory.getLogger(StashService::class.java)

    @Autowired
    private val apiService: ApiService? = null

    @Scheduled(cron = "*/5 * * * * *")
    fun updateStashes() {
        val stashes = apiService?.getPublicStashTabs()
        if (stashes != null) {
            val toSave = prepareToSave(stashes)
            if (toSave.isNotEmpty()) {
                log.info(toSave.size.toString())
                insertMany(toSave)
            }
        }
        log.info("============================================")
    }

    fun prepareToSave(stashes: List<Document>): List<Document> {
        val result = ArrayList<Document>()
        for (stash in stashes) {
            if (isPublic(stash)) {
                val items = getItems(stash)
                val verifiedItems = items.filter { it -> isVerified(it) }
                if (verifiedItems.isNotEmpty()) {
                    setItems(stash, verifiedItems)
                    result.add(stash)
                }
            }
        }
        return result
    }

    fun isPublic(stash: Document) = stash.getBoolean("public")

    @Suppress("UNCHECKED_CAST")
    fun getItems(stash: Document) = stash.get("items", List::class.java) as List<Document>
    fun setItems(stash: Document, items: List<Document>) = stash.put("items", items)

    fun isVerified(item: Document) = item.getBoolean("verified")

    init {
        if (mongoCollection.count().toInt() == 0) {
            mongoCollection.createIndex(Document("id", 1).append("unique", true))
        }
    }
}