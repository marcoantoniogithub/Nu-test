package com.example.nu.urlshortener.ui

import com.example.nu.urlshortener.domain.model.ShortenUrl

data class UrlShortenerState(
    val urlInput: String = "",
    val isLoading: Boolean = false,
    val recentUrls: List<ShortenUrl> = emptyList(),
    val error: String? = null
)

sealed class UrlShortenerIntent {
    data class UpdateUrlInput(val newUrl: String) : UrlShortenerIntent()
    object ShortenButtonClick : UrlShortenerIntent()
    data class UrlItemClicked(val url: ShortenUrl) : UrlShortenerIntent()
}

sealed class UrlShortenerEffect {
    data class ShowToast(val message: String) : UrlShortenerEffect()
    data class CopyUrlToClipboard(val urlToCopy: String) : UrlShortenerEffect()
}