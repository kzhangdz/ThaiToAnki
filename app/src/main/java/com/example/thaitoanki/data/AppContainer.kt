package com.example.thaitoanki.data

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.thaitoanki.data.anki.AnkiDroidHelper
import com.example.thaitoanki.data.anki.AnkiDroidRepository
import com.example.thaitoanki.data.network.HtmlConverterFactory.HtmlConverterFactory
import com.example.thaitoanki.data.database.WordsDatabase
import com.example.thaitoanki.data.database.WordsRepository
import com.example.thaitoanki.data.database.OfflineWordsRepository
import com.example.thaitoanki.data.network.NetworkThaiLanguageRepository
import com.example.thaitoanki.data.network.ThaiLanguageApiService
import com.example.thaitoanki.data.network.ThaiLanguageRepository
import retrofit2.Retrofit

interface AppContainer {
    // source api for language data
    val thaiLanguageRepository: ThaiLanguageRepository

    // database for words
    val wordsRepository: WordsRepository

    // place to save flashcards to. In our case, AnkiDroid
    val flashcardRepository: AnkiDroidRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl =
        "http://www.thai-language.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(HtmlConverterFactory(baseUrl))
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: ThaiLanguageApiService by lazy {
        retrofit.create(ThaiLanguageApiService::class.java)
    }

    override val thaiLanguageRepository: ThaiLanguageRepository by lazy {
        NetworkThaiLanguageRepository(retrofitService)
    }

    override val wordsRepository: WordsRepository by lazy {
        OfflineWordsRepository(
            wordDao = WordsDatabase.getDatabase(context).wordDao()
        )
    }

    // TODO: add the ankiDroidHelper here?
    // a new data source for thai language data would probably be another Container
    override val flashcardRepository: AnkiDroidRepository by lazy {
        val ankiDroidHelper = AnkiDroidHelper(context)
        AnkiDroidRepository(ankiDroidHelper)
    }
}
