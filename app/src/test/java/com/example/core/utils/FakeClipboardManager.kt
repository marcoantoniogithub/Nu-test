package com.example.core.utils

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import io.mockk.every
import io.mockk.mockk

class FakeClipboardManager : Clipboard {
    var capturedText: CharSequence? = null
    override val nativeClipboard: android.content.ClipboardManager
        get() = mockk {
            every { setPrimaryClip(any()) } answers {
                capturedText = firstArg<android.content.ClipData>().getItemAt(0).text
            }
        }

    override suspend fun getClipEntry(): ClipEntry? {
        return null
    }

    override suspend fun setClipEntry(clipEntry: ClipEntry?) {
    }
}