package com.example.iss.data

import com.google.gson.annotations.SerializedName

data class AstroPeopleResponse(
    @SerializedName("people")
    val people: List<AstroPerson>,
    @SerializedName("number")
    val number: Int,
    @SerializedName("message")
    val message: String
)

data class AstroPerson(
    @SerializedName("name")
    val name: String,
    @SerializedName("craft")
    val craft: String
)
