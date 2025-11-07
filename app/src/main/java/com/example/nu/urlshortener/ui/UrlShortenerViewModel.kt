package com.example.nu.urlshortener.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.usecase.ShortenUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UrlShortenerViewModel @Inject constructor(
    private val shortenUrlUseCase: ShortenUrlUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(UrlShortenerState())
    val state: StateFlow<UrlShortenerState> = _state.asStateFlow()

    private val _effect = Channel<UrlShortenerEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun processIntent(intent: UrlShortenerIntent) {
        when (intent) {
            is UrlShortenerIntent.UpdateUrlInput -> updateInput(intent.newUrl)
            is UrlShortenerIntent.ShortenButtonClick -> shortenUrl()
            is UrlShortenerIntent.LoadRecentUrls -> loadUrls()
            is UrlShortenerIntent.UrlItemClicked -> copyUrlToClipboard(intent.url)
        }
    }

    private fun copyUrlToClipboard(url: ShortenUrl) {
        viewModelScope.launch {
            _effect.send(UrlShortenerEffect.CopyUrlToClipboard(url.shortUrl))
            _effect.send(UrlShortenerEffect.ShowToast("URL encurtada copiada!"))
        }
    }

    private fun updateInput(newUrl: String) {
        _state.update { it.copy(urlInput = newUrl) }
    }

    private fun shortenUrl() {
        viewModelScope.launch {
            val longUrl = state.value.urlInput
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val shortened = shortenUrlUseCase(longUrl)
                _state.update { currentState ->
                    val newList = listOf(shortened) + currentState.recentUrls
                    currentState.copy(
                        isLoading = false,
                        urlInput = "",
                        recentUrls = newList
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Erro: ${e.message}") }
            }
        }
    }

    private fun loadUrls() {}
}