package com.example.nu.urlshortener.domain.usecase

import com.example.nu.urlshortener.domain.model.ShortenedUrl
import com.example.nu.urlshortener.domain.repository.UrlRepository
import javax.inject.Inject

class GetOriginalUrlUseCase @Inject constructor(
    private val repository: UrlRepository
) {
    suspend operator fun invoke(aliasId: String): String? {
        if (aliasId.isBlank()) {
            return null
        }
        val response = repository.getUrls(aliasId)

        return response.url
    }
}