package com.trios2024aa.itunes

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val retrofit = Retrofit.Builder()
    .baseUrl("https://itunes.apple.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val itunesService = retrofit.create(ApiService::class.java)

interface ApiService {
    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "all",
        @Query("limit") limit: Int = 50
    ): Response<ITunesResponse>
}
