package com.example.iss.data

import retrofit2.http.GET
import retrofit2.http.Path

data class WikipediaSummary(
    val title: String?,
    val description: String?,
    val extract: String?,
    val thumbnail: WikipediaThumbnail?
)

data class WikipediaThumbnail(
    val source: String?,
    val width: Int?,
    val height: Int?
)

interface WikipediaApiService {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getSummary(@Path("title") title: String): WikipediaSummary
}
