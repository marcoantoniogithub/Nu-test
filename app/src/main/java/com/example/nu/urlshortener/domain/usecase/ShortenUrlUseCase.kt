package com.example.nu.urlshortener.domain.usecase

import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.repository.UrlRepository
import javax.inject.Inject

class ShortenUrlUseCase @Inject constructor(
    private val repository: UrlRepository
) {
    suspend operator fun invoke(longUrl: String): ShortenUrl {
        if (longUrl.isBlank()) {
            throw IllegalArgumentException("URL cannot be empty")
        }
        return repository.shortenUrl(longUrl)
    }
}