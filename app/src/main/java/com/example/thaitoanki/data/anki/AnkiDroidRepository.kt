package com.example.thaitoanki.data.anki

import com.example.thaitoanki.data.database.WordDao
import java.util.LinkedList

class AnkiDroidRepository(
    private val ankiDroidHelper: AnkiDroidHelper
): AnkiRepository {
    override fun storeDeckReference(deckName: String?, deckId: Long) {
        ankiDroidHelper.storeDeckReference(deckName, deckId)
    }

    override fun storeModelReference(modelName: String?, modelId: Long) {
        ankiDroidHelper.storeModelReference(modelName, modelId)
    }

    override fun removeDuplicates(
        fields: LinkedList<Array<String>>,
        tags: LinkedList<Set<String?>?>,
        modelId: Long
    ) {
        ankiDroidHelper.removeDuplicates(fields, tags, modelId)
    }

    override fun findModelIdByName(modelName: String, numFields: Int): Long? {
        val modelId = ankiDroidHelper.findModelIdByName(modelName, numFields)
        return modelId
    }

    override fun findDeckIdByName(deckName: String): Long? {
        val deckId = ankiDroidHelper.findDeckIdByName(deckName)
        return deckId
    }

    override fun getDeckId(deckName: String): Long? {
        val deckId = ankiDroidHelper.findDeckIdByName(deckName)
        return deckId
    }

    override fun createDeck(deckName: String): Long? {
        val deckId = ankiDroidHelper.createDeck(deckName)
        return deckId
    }

    override fun createModel(modelName: String, deckId: Long): Long? {
        val modelId = ankiDroidHelper.createModel(modelName, deckId)
        return modelId
    }

    override fun addCardsToAnkiDroid(
        deckId: Long,
        modelId: Long,
        data: List<Map<String, String>>
    ): Int {
        val responseCode = ankiDroidHelper.addCardsToAnkiDroid(deckId, modelId, data)
        return responseCode
    }

}