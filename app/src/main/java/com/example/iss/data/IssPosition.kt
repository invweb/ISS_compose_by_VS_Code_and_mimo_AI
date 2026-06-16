package com.example.iss.data

import com.google.gson.annotations.SerializedName

data class IssPositionResponse(
    @SerializedName("iss_position")
    val issPosition: IssPosition,
    @SerializedName("timestamp")
    val timestamp: Long,
    @SerializedName("message")
    val message: String
)

data class IssPosition(
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String
) {
    fun latitudeDouble(): Double = latitude.toDoubleOrNull() ?: 0.0
    fun longitudeDouble(): Double = longitude.toDoubleOrNull() ?: 0.0
}
