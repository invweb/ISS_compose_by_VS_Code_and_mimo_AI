package com.example.iss.data

import retrofit2.http.GET

interface IssApiService {
    @GET("iss-now.json")
    suspend fun getIssPosition(): IssPositionResponse
}
