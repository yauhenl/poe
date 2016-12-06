package com.yauhenl.poe.service

import com.mongodb.client.MongoDatabase
import org.springframework.stereotype.Service

@Service
class AccountService(mongoDatabase: MongoDatabase) : BaseMongoService(mongoDatabase, "account") {

}