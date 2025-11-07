package com.example.nu

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.nu.urlshortener.domain.usecase.GetOriginalUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOriginalUrlUseCase: GetOriginalUrlUseCase
) : ViewModel() {

    suspend fun getOriginalUrl(id: String): String? {
        return try {
            val originalUrl = getOriginalUrlUseCase.invoke(id)
            Log.d("MainViewModel", "URL original encontrada: $originalUrl")
            originalUrl
        } catch (e: Exception) {
            Log.e("MainViewModel", "Erro ao buscar URL: ${e.message}")
            null
        }
    }
}