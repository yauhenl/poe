package com.yauhenl.poe.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SearchService {
    val stash = "stash"

    @Value("\${spring.data.elasticsearch.indexAlias}")
    private val indexAliasName: String? = null
}