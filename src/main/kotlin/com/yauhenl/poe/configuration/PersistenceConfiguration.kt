package com.yauhenl.poe.configuration

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PersistenceConfiguration {
    @Value("\${spring.data.mongodb.database}")
    private val mongoDb: String? = null

    @Bean
    open fun database(client: MongoClient): MongoDatabase {
        return client.getDatabase(mongoDb)
    }
}