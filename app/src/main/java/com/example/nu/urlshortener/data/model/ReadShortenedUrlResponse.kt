package com.example.nu.urlshortener.data.model


import com.google.gson.annotations.SerializedName

data class ReadShortenedUrlResponse(
    @SerializedName("url")
    val url: String

)
