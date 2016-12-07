package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import org.springframework.stereotype.Service
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled

@Service
class StashService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "stash") {

    @Autowired
    private val apiService: ApiService? = null

    override fun initCollection() {
        mongoCollection.createIndex(Document("id", 1))
    }

    @Scheduled(cron = "*/30 * * * * *")
    fun updateStashes() {
        val stashes = apiService?.getPublicStashTabs()
        if (stashes != null) {
            insertMany(stashes)
        }
    }
}