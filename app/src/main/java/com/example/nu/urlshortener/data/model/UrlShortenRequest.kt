package com.example.nu.urlshortener.data.model

import com.google.gson.annotations.SerializedName

data class UrlShortenRequest(
    @SerializedName("url")
    val url: String
)
