package com.example.nu.urlshortener.domain.model

data class ShortenUrl(
    val alias: String,
    val originalUrl: String,
    val shortUrl: String
)