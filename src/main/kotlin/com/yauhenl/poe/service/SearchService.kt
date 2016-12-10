package com.yauhenl.poe.service

import com.google.common.io.ByteStreams
import org.elasticsearch.client.Client
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class SearchService {
    val stash = "stash"

    @Value("\${spring.data.elasticsearch.indexAlias}")
    private val indexAliasName: String? = null

    @Autowired
    var elasticSearchClient: Client? = null

    @Scheduled(cron = "0 0/15 * * * *")
    fun reindexStashes() {
        val newIndexName = "$indexAliasName-${System.currentTimeMillis()}"
        createIndex(newIndexName)
        processStashes(newIndexName)
        createOrSwitchAlias(indexAliasName!!, newIndexName)
    }

    private fun createIndex(indexName: String) {
        elasticSearchClient?.admin()?.indices()?.prepareCreate(indexName)
                ?.setSource(ByteStreams.toByteArray(javaClass.classLoader.getResourceAsStream("es_index_source.json")))
                ?.execute()?.actionGet()
    }

    private fun createOrSwitchAlias(aliasName: String, indexName: String) {
        val indices = elasticSearchClient?.admin()?.indices()!!
        val aliasExists = indices.prepareAliasesExist(aliasName)?.execute()?.actionGet()?.isExists as Boolean
        val aliases = indices.prepareAliases()
        if (aliasExists) {
            indices.prepareGetAliases(aliasName)?.execute()?.actionGet()?.aliases?.forEach { cursor ->
                val key = cursor.key
                val value = cursor.value
                value.forEach { aliasMetaData -> aliases?.removeAlias(key, aliasMetaData.alias()) }
            }
        }
        aliases?.addAlias(indexName, aliasName)
        aliases?.execute()?.actionGet()
    }

    private fun processStashes(indexName: String) {

    }
}