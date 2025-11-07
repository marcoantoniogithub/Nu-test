package com.example.nu.urlshortener.data.model

import com.google.gson.annotations.SerializedName

data class UrlShortenResponse(
    @SerializedName("alias")
    val alias: String,
    @SerializedName("_links")
    val links: LinksDto
)
