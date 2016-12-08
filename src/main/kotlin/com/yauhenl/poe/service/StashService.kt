package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StashService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "stash") {
    private val log: Logger = LoggerFactory.getLogger(StashService::class.java)

    @Autowired
    private val apiService: ApiService? = null

    @Scheduled(cron = "*/5 * * * * *")
    fun updateStashes() {
        log.info("updateStashes start" + LocalDateTime.now().toString())
        val stashes = apiService?.getPublicStashTabs()
        if (stashes != null) {
            val toSave = stashes.filter { it ->
                isPublic(it) && !getItems(it).isEmpty()
            }
            log.info(toSave.size.toString())
            insertMany(toSave)
        }
        log.info("updateStashes end" + LocalDateTime.now().toString())
    }

    fun isPublic(stash: Document) = stash.getBoolean("public")

    fun getItems(stash: Document) = stash.get("items", List::class.java)

    init {
        if (mongoCollection.count().toInt() == 0) {
            mongoCollection.createIndex(Document("id", 1).append("unique", true))
        }
    }
}