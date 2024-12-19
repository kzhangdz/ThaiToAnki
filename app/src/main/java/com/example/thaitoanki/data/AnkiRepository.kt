package com.example.thaitoanki.data

import com.example.thaitoanki.network.ThaiLanguageApiService
import org.jsoup.nodes.Document

//interface ThaiLanguageRepository {
//    suspend fun searchDictionary(word: String): Document
//
//    suspend fun getWord(wordId: Int): Document
//}

//https://medium.com/@michaelangelo.reyes19/building-a-data-layer-in-android-using-the-repository-pattern-177cb21e4115#:~:text=One%20of%20the%20most%20effective,modular%2C%20maintainable%2C%20and%20testable.
// can probably make a repository without a retrofit api service
// can pass in a Room db service later

// put functions that trigger the ankidroid api here

// TODO: pass this into the ThaiViewModel

class AnkiRepository {



//    suspend fun searchDictionary(word: String): Document {
//        return thaiLanguageApiService.searchDictionary(word)
//    }
//
//    override suspend fun getWord(wordId: Int): Document {
//        return thaiLanguageApiService.getWord(wordId)
//    }
}