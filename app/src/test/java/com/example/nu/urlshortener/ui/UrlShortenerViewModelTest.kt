package com.example.nu.urlshortener.ui

import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.usecase.ShortenUrlUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UrlShortenerViewModelTest {

    private lateinit var mockShortenUrlUseCase: ShortenUrlUseCase
    private lateinit var viewModel: UrlShortenerViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val OLD_VALID_LONG_URL = "https://www.kotlinold.org"
    private val OLD_VALID_SHORT_URL = "https://nu.appold/kt01"

    private val OLD_MOCK_SHORTEN_URL_MODEL = ShortenUrl(
        alias = "kt01",
        originalUrl = OLD_VALID_LONG_URL,
        shortUrl = OLD_VALID_SHORT_URL
    )
    private val VALID_LONG_URL = "https://www.kotlin.org"
    private val VALID_SHORT_URL = "https://nu.app/kt01"
    private val MOCK_SHORTEN_URL_MODEL = ShortenUrl(
        alias = "kt01",
        originalUrl = VALID_LONG_URL,
        shortUrl = VALID_SHORT_URL
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockShortenUrlUseCase = mockk()
        viewModel = UrlShortenerViewModel(mockShortenUrlUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `UpdateUrlInput intent should update urlInput state`() = runTest {
        val newUrl = "https://www.example.com"

        viewModel.processIntent(UrlShortenerIntent.UpdateUrlInput(newUrl))

        assertEquals(newUrl, viewModel.state.value.urlInput)
    }


    @Test
    fun `ShortenButtonClick success should update state`() = runTest {
        val initialUrl = VALID_LONG_URL
        val initialList = listOf(OLD_MOCK_SHORTEN_URL_MODEL)

        viewModel._state.update {
            it.copy(urlInput = initialUrl, recentUrls = initialList)
        }

        coEvery { mockShortenUrlUseCase.invoke(initialUrl) } returns MOCK_SHORTEN_URL_MODEL

        viewModel.processIntent(UrlShortenerIntent.ShortenButtonClick)

        assertEquals(true, viewModel.state.value.isLoading)

        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertEquals(false, finalState.isLoading)
        assertEquals("", finalState.urlInput)
        assertEquals(null, finalState.error)

        assertEquals(2, finalState.recentUrls.size)
        assertEquals(MOCK_SHORTEN_URL_MODEL, finalState.recentUrls.first())

        coVerify(exactly = 1) { mockShortenUrlUseCase.invoke(initialUrl) }
    }


    @Test
    fun `ShortenButtonClick failure (IOException) should set error state`() = runTest {
        val initialUrl = VALID_LONG_URL
        val errorMessage = "Network Down"

        viewModel._state.update { it.copy(urlInput = initialUrl) }

        coEvery { mockShortenUrlUseCase.invoke(initialUrl) } throws IOException(errorMessage)

        viewModel.processIntent(UrlShortenerIntent.ShortenButtonClick)

        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertEquals(false, finalState.isLoading)
        assertEquals("Erro: $errorMessage", finalState.error)
        assertEquals(initialUrl, finalState.urlInput)

        coVerify(exactly = 1) { mockShortenUrlUseCase.invoke(initialUrl) }
    }


    @Test
    fun `UrlItemClicked intent should emit CopyUrlToClipboard and ShowToast effects`() = runTest {
        val emittedEffects = mutableListOf<UrlShortenerEffect>()
        val job = launch {
            viewModel.effect.take(2).toList(emittedEffects)
        }

        viewModel.processIntent(UrlShortenerIntent.UrlItemClicked(MOCK_SHORTEN_URL_MODEL))

        advanceUntilIdle()

        assertEquals(2, emittedEffects.size)

        val effect1 = emittedEffects[0] as UrlShortenerEffect.CopyUrlToClipboard
        assertEquals(VALID_SHORT_URL, effect1.urlToCopy)

        val effect2 = emittedEffects[1] as UrlShortenerEffect.ShowToast
        assertEquals("URL encurtada copiada!", effect2.message)

        job.cancel()
    }
}