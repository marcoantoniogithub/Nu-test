package com.example.nu.presentation.urlshortener

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.ui.UrlShortenerIntent
import com.example.nu.urlshortener.ui.UrlShortenerViewModel
import com.example.nu.core.theme.NuTheme
import com.example.nu.urlshortener.ui.UrlShortenerEffect
import kotlinx.coroutines.launch

val SkeletonLightGray = Color(0xFFEEEEEE)
val SkeletonDarkGray = Color(0xFFDDDDDD)

@Composable
fun UrlShortenerScreen(
    viewModel: UrlShortenerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UrlShortenerEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is UrlShortenerEffect.CopyUrlToClipboard -> {
                    scope.launch {
                        clipboardManager.nativeClipboard.setPrimaryClip(
                            android.content.ClipData.newPlainText(
                                "URL",
                                effect.urlToCopy
                            )
                        )
                    }
                }
            }
        }
    }



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            UrlInputBar(
                urlText = state.urlInput,
                onTextChange = { newUrl ->
                    viewModel.processIntent(UrlShortenerIntent.UpdateUrlInput(newUrl))
                },
                onShortenClick = {
                    viewModel.processIntent(UrlShortenerIntent.ShortenButtonClick)
                },
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recently shortened URLs",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecentlyShortenedUrlsList(
                urls = state.recentUrls,
                onItemClick = { url: ShortenUrl ->
                    viewModel.processIntent(UrlShortenerIntent.UrlItemClicked(url))
                }
            )
        }
    }
}

@Composable
fun UrlInputBar(
    urlText: String,
    onTextChange: (String) -> Unit,
    onShortenClick: () -> Unit,
    isLoading: Boolean
) {
    OutlinedTextField(
        value = urlText,
        onValueChange = onTextChange,
        label = { Text("URL to shorten") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        trailingIcon = {
            if (isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp))
            } else {
                IconButton(
                    onClick = onShortenClick,
                    enabled = urlText.isNotBlank(),
                    modifier = Modifier
                        .padding(4.dp)
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = if (urlText.isNotBlank()) 1f else 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Shorten URL",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun RecentlyShortenedUrlsList(
    urls: List<ShortenUrl>,
    onItemClick: (ShortenUrl) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        if (urls.isEmpty()) {
            items(5) {
                ShortenedUrlPlaceholderItem()
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        } else {
            items(urls) { url ->
                UrlListItem(
                    url = url,
                    onItemClick = onItemClick
                )
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ShortenedUrlPlaceholderItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(14.dp)
                .background(SkeletonDarkGray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(10.dp)
                .background(SkeletonLightGray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(10.dp)
                .background(SkeletonLightGray, RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun UrlListItem(
    url: ShortenUrl,
    onItemClick: (ShortenUrl) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(url)
            }
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = url.shortUrl,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Original: ${url.originalUrl}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = "Alias: ${url.alias}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUrlShortenerScreen() {
    NuTheme {
        ShortenedUrlPlaceholderItem()
    }
}