package com.example.nu

import android.util.Log
import com.example.nu.urlshortener.domain.usecase.GetOriginalUrlUseCase
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.Exception

class MainViewModelTest {

    private val mockGetOriginalUrlUseCase = mockk<GetOriginalUrlUseCase>()
    private lateinit var viewModel: MainViewModel

    private val TEST_ID = "testAlias"
    private val MOCK_URL = "https://www.original.com/long/path"
    private val MOCK_ERROR_MESSAGE = "Network timeout"

    @Before
    fun setup() {
        viewModel = MainViewModel(mockGetOriginalUrlUseCase)

        mockkStatic(Log::class)
        coEvery { Log.d(any(), any()) } returns 0
        coEvery { Log.e(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getOriginalUrl retorna URL e loga sucesso quando use case eh bem-sucedido`() = runTest {
        coEvery { mockGetOriginalUrlUseCase.invoke(TEST_ID) } returns MOCK_URL

        val result = viewModel.getOriginalUrl(TEST_ID)

        assertEquals(MOCK_URL, result)

        coVerify(exactly = 1) { mockGetOriginalUrlUseCase.invoke(TEST_ID) }
        coVerify(exactly = 1) { Log.d("MainViewModel", "URL original encontrada: $MOCK_URL") }
    }

    @Test
    fun `getOriginalUrl retorna null e loga erro quando use case falha com excecao`() = runTest {
        val exception = Exception(MOCK_ERROR_MESSAGE)
        coEvery { mockGetOriginalUrlUseCase.invoke(TEST_ID) } throws exception

        val result = viewModel.getOriginalUrl(TEST_ID)

        assertEquals(null, result)

        coVerify(exactly = 1) { mockGetOriginalUrlUseCase.invoke(TEST_ID) }
        coVerify(exactly = 1) { Log.e("MainViewModel", "Erro ao buscar URL: $MOCK_ERROR_MESSAGE") }
        coVerify(exactly = 0) { Log.d(any(), any()) }
    }
}