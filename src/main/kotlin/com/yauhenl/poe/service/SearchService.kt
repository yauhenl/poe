package com.yauhenl.poe.service

import com.yauhenl.poe.domain.Stash.getAccountName
import com.yauhenl.poe.domain.Stash.getId
import com.yauhenl.poe.domain.Stash.getLastCharacterName
import org.bson.Document
import org.elasticsearch.client.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
open class SearchService {
    val logger: Logger = LoggerFactory.getLogger(SearchService::class.java)
    val stashType = "stash"

    @Value("\${spring.data.elasticsearch.indexAlias}")
    private val indexAliasName: String? = null

    @Autowired
    var elasticSearchClient: Client? = null

    @Autowired
    val stashService: StashService? = null

    @Scheduled(fixedRate = 15 * 60 * 1000)
//    @Async
    fun reindexStashes() {
        val newIndexName = "$indexAliasName-${System.currentTimeMillis()}"
        logger.info(newIndexName)
        createIndex(newIndexName)
        processStashes(newIndexName)
        createOrSwitchAlias(indexAliasName!!, newIndexName)
    }

    private fun createIndex(indexName: String) {
        elasticSearchClient?.admin()?.indices()?.prepareCreate(indexName)?.execute()?.actionGet()
    }

    private fun createOrSwitchAlias(aliasName: String, indexName: String) {
        val indices = elasticSearchClient?.admin()?.indices()!!
        val aliasExists = indices.prepareAliasesExist(aliasName)?.execute()?.actionGet()?.isExists as Boolean
        val aliases = indices.prepareAliases()
        if (aliasExists) {
            indices.prepareGetAliases(aliasName)?.execute()?.actionGet()?.aliases?.forEach { cursor ->
                cursor.value.forEach { aliasMetaData ->
                    aliases?.removeAlias(cursor.key, aliasMetaData.alias())
                }
            }
        }
        aliases?.addAlias(indexName, aliasName)
        aliases?.execute()?.actionGet()
    }

    private fun processStashes(indexName: String) {
        stashService?.find()?.forEach { stash ->
            elasticSearchClient?.prepareIndex(indexName, stashType, getId(stash))?.setSource(processStash(stash))?.execute()?.actionGet(5, TimeUnit.SECONDS)
        }
    }

    private fun processStash(stash: Document): HashMap<String, Any> {
        val result = HashMap<String, Any>()
        result.put("id", getId(stash))
        result.put("accountName", getAccountName(stash))
        result.put("lastCharacterName", getLastCharacterName(stash))
        return result
    }
}