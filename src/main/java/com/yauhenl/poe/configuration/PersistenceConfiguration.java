package com.yauhenl.poe.configuration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String mongoDb;

    @Bean
    public MongoDatabase database(MongoClient client) {
        return client.getDatabase(mongoDb);
    }
}
