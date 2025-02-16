package com.example.thaitoanki.data.network

import org.jsoup.nodes.Document

interface ThaiLanguageRepository {
    suspend fun searchDictionary(word: String): Document

    suspend fun getWord(wordId: Int): Document
}

class NetworkThaiLanguageRepository(
    private val thaiLanguageApiService: ThaiLanguageApiService
) : ThaiLanguageRepository {
    override suspend fun searchDictionary(word: String): Document {
        return thaiLanguageApiService.searchDictionary(word)
    }

    override suspend fun getWord(wordId: Int): Document {
        return thaiLanguageApiService.getWord(wordId)
    }
}