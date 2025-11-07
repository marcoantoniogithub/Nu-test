package com.example.nu.urlshortener.domain.usecase

import com.example.nu.urlshortener.domain.model.ShortenedUrl
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

class GetOriginalUrlUseCaseTest {

    private lateinit var mockRepository: UrlRepository
    private lateinit var useCase: GetOriginalUrlUseCase

    private val VALID_ALIAS = "abc12345"
    private val ORIGINAL_URL = "https://www.google.com/search?q=kotlin"
    private val SHORTENED_URL_MODEL = ShortenedUrl(url = ORIGINAL_URL)

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = GetOriginalUrlUseCase(mockRepository)
    }


    @Test
    fun `invoke with valid alias should return the original URL`() = runTest {
        coEvery { mockRepository.getUrls(VALID_ALIAS) } returns SHORTENED_URL_MODEL

        val result = useCase(VALID_ALIAS)

        assertEquals(ORIGINAL_URL, result)
        coVerify(exactly = 1) { mockRepository.getUrls(VALID_ALIAS) }
    }

    @Test
    fun `invoke with blank aliasId should return null and NOT call repository`() = runTest {
        val blankAlias = "   "

        val result = useCase(blankAlias)

        assertEquals(null, result)
        coVerify(exactly = 0) { mockRepository.getUrls(any()) }
    }

    @Test
    fun `invoke with empty aliasId should return null and NOT call repository`() = runTest {
        val emptyAlias = ""

        val result = useCase(emptyAlias)

        assertEquals(null, result)
        coVerify(exactly = 0) { mockRepository.getUrls(any()) }
    }

    @Test(expected = IOException::class)
    fun `invoke should propagate IOException from repository`() = runTest {
        coEvery { mockRepository.getUrls(VALID_ALIAS) } throws IOException("Repository network error")

        val result = useCase(VALID_ALIAS)

        assertThrows(IOException::class.java) { result }

        coVerify(exactly = 1) { mockRepository.getUrls(VALID_ALIAS) }
    }

    @Test(expected = RuntimeException::class)
    fun `invoke should propagate API exception from repository`() = runTest {
        val apiException = RuntimeException("404 Not Found")
        coEvery { mockRepository.getUrls(VALID_ALIAS) } throws apiException

        val result = useCase(VALID_ALIAS)
        assertThrows(RuntimeException::class.java) { result }

        coVerify(exactly = 1) { mockRepository.getUrls(VALID_ALIAS) }
    }
}