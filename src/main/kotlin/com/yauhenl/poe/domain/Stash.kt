package com.yauhenl.poe.domain

import org.bson.Document

object Stash {
    fun isPublic(stash: Document) = stash.getBoolean("public")

    @Suppress("UNCHECKED_CAST")
    fun getItems(stash: Document) = stash.get("items", List::class.java) as List<Document>

    fun setItems(stash: Document, items: List<Document>) = stash.put("items", items)

    fun getId(stash: Document) = stash.getString("id")

    fun isVerified(item: Document) = item.getBoolean("verified")

    fun getAccountName(stash: Document) = stash.getString("accountName")

    fun getLastCharacterName(stash: Document) = stash.getString("lastCharacterName")
}