package com.example.nu.presentation.urlshortener

import android.content.Context
import android.os.Build
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.core.utils.FakeClipboardManager
import com.example.core.utils.TestApp
import com.example.nu.urlshortener.domain.model.ShortenUrl
import com.example.nu.urlshortener.ui.UrlShortenerEffect
import com.example.nu.urlshortener.ui.UrlShortenerIntent
import com.example.nu.urlshortener.ui.UrlShortenerState
import com.example.nu.urlshortener.ui.UrlShortenerViewModel
import io.mockk.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU], manifest = Config.NONE, application = TestApp::class)
class UrlShortenerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val MOCK_URL = ShortenUrl("a1", "long.com/1", "short.co/1")

    private val mockContext = mockk<Context>(relaxed = true)
    private val fakeClipboardManager = FakeClipboardManager()

    private val mockViewModel = mockk<UrlShortenerViewModel>(relaxed = true)
    private val mutableState = MutableStateFlow(UrlShortenerState())
    private val mutableEffect = Channel<UrlShortenerEffect>(Channel.BUFFERED)
    private val capturedIntents = mutableListOf<UrlShortenerIntent>()

    @Before
    fun setup() {
        every { mockViewModel.state } returns mutableState.asStateFlow()
        every { mockViewModel.effect } returns mutableEffect.receiveAsFlow()

        every { mockViewModel.processIntent(capture(capturedIntents)) } answers {
            val intent = it.invocation.args[0] as UrlShortenerIntent
            if (intent is UrlShortenerIntent.UpdateUrlInput) {
                mutableState.update { currentState -> currentState.copy(urlInput = intent.newUrl) }
            }
        }

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalContext provides mockContext,
                LocalClipboard provides fakeClipboardManager as Clipboard
            ) {
                UrlShortenerScreen(viewModel = mockViewModel)
            }
        }
    }


    @Test
    fun screen_typingInput_updatesStateAndSendsUpdateIntent() {
        val newUrl = "https://new.url"
        capturedIntents.clear()

        composeTestRule.onNodeWithText("URL to shorten").performTextInput(newUrl)

        composeTestRule.onNodeWithText(newUrl).assertIsDisplayed()

        assertEquals(1, capturedIntents.size)
        assertEquals(UrlShortenerIntent.UpdateUrlInput(newUrl), capturedIntents.first())
        assertEquals(newUrl, mutableState.value.urlInput)
    }

    @Test
    fun screen_shortenButtonClick_sendsShortenIntent() {
        val url = "https://valid.url"

        mutableState.update { it.copy(urlInput = url) }
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasContentDescription("Shorten URL")).performClick()

        verify(exactly = 1) { mockViewModel.processIntent(UrlShortenerIntent.ShortenButtonClick) }
    }
    @Test
    fun `screen loadingState showsCircularProgressIndicator`() {
        mutableState.update { it.copy(isLoading = true) }
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasContentDescription("Shorten URL")).assertDoesNotExist()
        composeTestRule.onNodeWithTag(CIRCULAR_PROGRESS_INDICATOR_TAG).assertIsDisplayed()
    }

    @Test
    fun screen_dataLoaded_displaysUrlListItemAndOriginalUrl() {
        val urls = listOf(MOCK_URL)

        mutableState.update { it.copy(recentUrls = urls) }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MOCK_URL.shortUrl).assertIsDisplayed()
        composeTestRule.onNodeWithText("Original: ${MOCK_URL.originalUrl}").assertIsDisplayed()
    }

    @Test
    fun screen_urlItemClick_sendsUrlItemClickedIntent() {
        val urls = listOf(MOCK_URL)

        mutableState.update { it.copy(recentUrls = urls) }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(MOCK_URL.shortUrl).performClick()

        verify(exactly = 1) { mockViewModel.processIntent(UrlShortenerIntent.UrlItemClicked(MOCK_URL)) }
    }

    @Test
    fun `screen copyUrlToClipboardEffect setsClipboardData`() = runTest {
            val urlToCopy = "https://short.co/test_copy"

            mutableEffect.trySend(UrlShortenerEffect.CopyUrlToClipboard(urlToCopy))

            advanceUntilIdle()

            composeTestRule.waitForIdle()

            assertEquals(urlToCopy, fakeClipboardManager.capturedText)
        }
}