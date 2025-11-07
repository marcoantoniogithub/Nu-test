package com.example.nu.urlshortener.data.model

import com.google.gson.annotations.SerializedName

data class UrlReadResponse(
    @SerializedName("url")
    val originalUrl: String
)
