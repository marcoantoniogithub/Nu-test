package com.example.nu.urlshortener.data.repository

import com.example.nu.urlshortener.data.model.LinksDto
import com.example.nu.urlshortener.data.model.ReadShortenedUrlResponse
import com.example.nu.urlshortener.data.model.UrlShortenRequest
import com.example.nu.urlshortener.data.model.UrlShortenResponse
import com.example.nu.urlshortener.data.service.UrlShortenerService
import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.domain.model.ShortenedUrl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UrlRepositoryImplTest {

    private lateinit var mockService: UrlShortenerService
    private lateinit var repository: UrlRepositoryImpl

    private val LONG_URL = "https://www.google.com/very/long/path"
    private val SHORT_URL = "https://url-shortener-server.onrender.com/12345"
    private val ALIAS = "12345"

    @Before
    fun setup() {
        mockService = mockk()
        repository = UrlRepositoryImpl(mockService)
    }


    @Test
    fun `shortenUrl with a valid URL should call service and return mapped ShortenUrl`() = runTest {
        val expectedRequest = UrlShortenRequest(url = LONG_URL)
        val mockResponse = UrlShortenResponse(
            alias = ALIAS,
            links = LinksDto(originalUrl = LONG_URL, shortUrl = SHORT_URL)
        )
        val expectedDomainModel = ShortenUrl(
            alias = ALIAS,
            originalUrl = LONG_URL,
            shortUrl = SHORT_URL
        )

        coEvery { mockService.postShortenUrl(expectedRequest) } returns mockResponse

        val result = repository.shortenUrl(LONG_URL)

        assertEquals(expectedDomainModel, result)
        coVerify(exactly = 1) { mockService.postShortenUrl(expectedRequest) }
    }

    @Test(expected = IOException::class)
    fun `shortenUrl network failure simulation should throw IOException`() = runTest {
        val expectedRequest = UrlShortenRequest(url = LONG_URL)

        coEvery { mockService.postShortenUrl(expectedRequest) } throws IOException("Network Down")

        val result = repository.shortenUrl(LONG_URL)
        assertThrows(IOException::class.java) { result }

        coVerify(exactly = 1) { mockService.postShortenUrl(expectedRequest) }
    }


    @Test
    fun `getUrls with a valid ID should call service and return mapped ShortenedUrl`() = runTest {
        val id = "uniqueAliasID"
        val originalUrlFromApi = "https://original.com"
        val mockResponse = ReadShortenedUrlResponse(url = originalUrlFromApi)
        val expectedDomainModel = ShortenedUrl(url = originalUrlFromApi)

        coEvery { mockService.getShortenUrl(id) } returns mockResponse

        val result = repository.getUrls(id)

        assertEquals(expectedDomainModel, result)
        coVerify(exactly = 1) { mockService.getShortenUrl(id) }
    }

    @Test(expected = RuntimeException::class)
    fun `getUrls with a non-existent ID should throw API exception`() = runTest {
        val nonExistentId = "404NotFound"

        coEvery { mockService.getShortenUrl(nonExistentId) } throws RuntimeException("HTTP 404 Not Found")

        val result = repository.getUrls(nonExistentId)
        assertThrows(RuntimeException::class.java) { result }

        coVerify(exactly = 1) { mockService.getShortenUrl(nonExistentId) }
    }
}