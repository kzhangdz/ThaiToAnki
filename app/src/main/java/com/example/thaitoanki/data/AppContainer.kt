package com.example.thaitoanki.data

import com.example.thaitoanki.data.HtmlConverterFactory.HtmlConverterFactory
import com.example.thaitoanki.network.ThaiLanguageApiService
import retrofit2.Retrofit

interface AppContainer {
    val thaiLanguageRepository: ThaiLanguageRepository
}

class DefaultAppContainer : AppContainer {
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

    // TODO: add the ankiDroidHelper here?
    // a new data source for thai language data would probably be another Container
}
