package com.example.nu.urlshortener.domain.usecase

import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.repository.UrlRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ShortenUrlUseCaseTest {

    private lateinit var mockRepository: UrlRepository
    private lateinit var useCase: ShortenUrlUseCase

    private val VALID_LONG_URL = "https://www.kotlin.org/docs"
    private val SHORT_URL_MODEL = ShortenUrl(
        alias = "kt2025",
        originalUrl = VALID_LONG_URL,
        shortUrl = "https://nu.app/kt2025"
    )

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = ShortenUrlUseCase(mockRepository)
    }

    @Test
    fun `invoke with valid URL should call repository and return ShortenUrl`() = runTest {
        coEvery { mockRepository.shortenUrl(VALID_LONG_URL) } returns SHORT_URL_MODEL

        val result = useCase(VALID_LONG_URL)

        assertEquals(SHORT_URL_MODEL, result)
        coVerify(exactly = 1) { mockRepository.shortenUrl(VALID_LONG_URL) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke with empty URL should throw IllegalArgumentException and NOT call repository`() = runTest {
        val emptyUrl = ""
        val result = useCase(emptyUrl)
        assertThrows(IllegalArgumentException::class.java) { result }

        coVerify(exactly = 0) { mockRepository.shortenUrl(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `invoke with blank URL should throw IllegalArgumentException and NOT call repository`() = runTest {
        val blankUrl = "    \t  "
        val result = useCase(blankUrl)
        assertThrows(IllegalArgumentException::class.java) { result }

        coVerify(exactly = 0) { mockRepository.shortenUrl(any()) }
    }

    @Test(expected = IOException::class)
    fun `invoke should propagate IOException from repository`() = runTest {
        coEvery { mockRepository.shortenUrl(VALID_LONG_URL) } throws IOException("Network connection lost")
        val result = useCase(VALID_LONG_URL)
        assertThrows(IOException::class.java) { result }

        coVerify(exactly = 1) { mockRepository.shortenUrl(VALID_LONG_URL) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke should propagate API exception from repository`() = runTest {
        val apiException = RuntimeException("400 Invalid URL Format")
        coEvery { mockRepository.shortenUrl(VALID_LONG_URL) } throws apiException

        val result = useCase(VALID_LONG_URL)

        assertThrows(RuntimeException::class.java) { result }

        coVerify(exactly = 1) { mockRepository.shortenUrl(VALID_LONG_URL) }
    }
}