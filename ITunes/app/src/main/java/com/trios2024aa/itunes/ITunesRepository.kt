package com.trios2024aa.itunes

import retrofit2.Response

class ITunesRepository(private val apiService: ApiService) {

    // Function to perform a search and return the response
    suspend fun searchTunes(query: String): Response<ITunesResponse> {
        return apiService.search(term = query)
    }
}
