package com.example.thaitoanki.network

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

import org.jsoup.nodes.Document;

/*

 */

interface ThaiLanguageApiService {
    @FormUrlEncoded
    @POST("dict")
    suspend fun searchDictionary(@Field("search") word: String): Document

    @GET("id/{wordId}")
    suspend fun getWord(@Path("wordId") wordId: Int): Document
}