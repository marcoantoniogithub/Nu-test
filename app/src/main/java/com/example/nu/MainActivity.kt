package com.example.nu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.nu.core.theme.NuTheme
import com.example.nu.presentation.urlshortener.UrlShortenerScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntentData(intent)

        enableEdgeToEdge()
        setContent {
            NuTheme {
                UrlShortenerScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.let { handleIntentData(it) }
    }

    private fun handleIntentData(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {

            val uri: Uri? = intent.data

            uri?.let {
                Log.d("DeepLink", "URI recebida: $it")

                val shortUrlId = it.lastPathSegment

                if (shortUrlId != null) {
                    lifecycleScope.launch {

                        val originalUrl = viewModel.getOriginalUrl(shortUrlId)

                        if (originalUrl != null) {
                            openInBrowser(originalUrl)
                        } else {
                            Log.e("MainActivity", "URL original não encontrada ou erro na API. Voltando à tela principal.")
                        }
                    }
                    intent.data = null
                }
            }
        }
    }

    private fun openInBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = url.toUri()
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.e("MainActivity", "Nenhum navegador encontrado para abrir a URL: $url")
        }
    }
}