package com.example.nu.urlshortener.data.repository

import com.example.nu.urlshortener.data.model.UrlShortenRequest
import com.example.nu.urlshortener.data.service.UrlShortenerService
import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.model.ShortenedUrl
import com.example.nu.urlshortener.domain.repository.UrlRepository
import javax.inject.Inject

class UrlRepositoryImpl @Inject constructor(
    private val service: UrlShortenerService
) : UrlRepository {

    override suspend fun shortenUrl(longUrl: String): ShortenUrl {
        val request = UrlShortenRequest(url = longUrl)

        val response = service.postShortenUrl(request)

        return ShortenUrl(
            alias = response.alias,
            originalUrl = response.links.originalUrl,
            shortUrl = response.links.shortUrl
        )
    }

    override suspend fun getUrls(id: String): ShortenedUrl {
        val response = service.getShortenUrl(id)

        return ShortenedUrl(
            response.url
        )

    }
}