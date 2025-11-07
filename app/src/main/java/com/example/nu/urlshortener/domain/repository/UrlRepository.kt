package com.example.nu.urlshortener.domain.repository

import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.model.ShortenedUrl

interface UrlRepository {
    suspend fun shortenUrl(longUrl: String): ShortenUrl
    suspend fun getUrls(id: String): ShortenedUrl
}