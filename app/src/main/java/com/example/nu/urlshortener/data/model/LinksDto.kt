package com.example.nu.urlshortener.data.model

import com.google.gson.annotations.SerializedName
data class LinksDto(
    @SerializedName("self")
    val originalUrl: String,
    @SerializedName("short")
    val shortUrl: String
)
