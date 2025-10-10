package com.cybomb.allmediadownloader.datamodels

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cybomb.allmediadownloader.fetcher.FacebookFetcher
import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
import com.cybomb.allmediadownloader.fetcher.PinterestFetcher
import com.cybomb.allmediadownloader.fetcher.RedditFetcher
import com.cybomb.allmediadownloader.fetcher.TwitterFetcher
import com.cybomb.allmediadownloader.fetcher.YouTubeFetcher
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Data class to represent the UI state
data class DownloaderState(
    val url: TextFieldValue = TextFieldValue(""),
    val isLoading: Boolean = false,
    val infoMsg: String? = null,
    val selectedPlatform: String = "Instagram",
    val showWebView: Boolean = false
)

class DownloaderViewModel(
    private val instagramFetcher: InstagramFetcher,
    private val youTubeFetcher: YouTubeFetcher,
    private val pinterestFetcher: PinterestFetcher,
    private val facebookFetcher: FacebookFetcher,
    private val redditFetcher: RedditFetcher,
    private val twitterFetcher: TwitterFetcher,
    private val getCookiesForUrl: (String) -> String?,
    private val startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
) : ViewModel() {

    private val _state = MutableStateFlow(DownloaderState())
    val state: StateFlow<DownloaderState> = _state

    private val allPlatforms = listOf("Instagram", "YouTube", "Pinterest", "Facebook", "Twitter", "Reddit", "LinkedIn", "Dailymotion", "Vimeo")
    private val webViewSupportedPlatforms = listOf("Instagram", "YouTube", "Pinterest", "Facebook", "Twitter", "Reddit", "LinkedIn", "Dailymotion", "Vimeo")

    fun updateUrl(newUrl: TextFieldValue) {
        _state.update { it.copy(url = newUrl) }
    }

    fun selectPlatform(platform: String) {
        _state.update { it.copy(selectedPlatform = platform, url = TextFieldValue(""), infoMsg = null) }
    }

    fun toggleWebView(show: Boolean) {
        _state.update { it.copy(showWebView = show) }
    }

    fun onWebViewFinished(result: WebViewResult) {
        toggleWebView(false)
        if (result.success) {
            // Note: Toast is kept here for minimal change, but ideally UI events are externalized or managed via SnackBar state.
            // A more robust solution would be to pass a 'showToast' event upstream.
        }
    }

    fun downloadMedia(context: Context) {
        val link = _state.value.url.text.trim()
        val selectedPlatform = _state.value.selectedPlatform

        if (link.isEmpty()) {
            Toast.makeText(context, "Please enter a valid URL", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, infoMsg = "Extracting media from $selectedPlatform...") }

            val mediaInfo = when (selectedPlatform) {
                "Instagram" -> {
                    val cookies = getCookiesForUrl(link)
                    instagramFetcher.fetchMediaDetails(link, cookies)
                }
                "YouTube" -> {
                    val cookies = getCookiesForUrl(link)
                    youTubeFetcher.fetchMediaDetails(link, cookies)
                }
                "Pinterest" -> {
                    val cookies = getCookiesForUrl(link)
                    pinterestFetcher.fetchMediaDetails(link, cookies)
                }
                "Facebook" -> {
                    val cookies = getCookiesForUrl(link)
                    facebookFetcher.fetchMediaDetails(link, cookies)
                }
                "Twitter" -> {
                    val cookies = getCookiesForUrl(link)
                    twitterFetcher.fetchMediaDetails(link, cookies)
                }
                "Reddit" -> {
                    val cookies = getCookiesForUrl(link)
                    redditFetcher.fetchMediaDetails(link, cookies)
                }
                "LinkedIn" -> {
                    _state.update { it.copy(infoMsg = "LinkedIn downloader coming soon!") }
                    null
                }
                "Dailymotion" -> {
                    _state.update { it.copy(infoMsg = "Dailymotion downloader coming soon!") }
                    null
                }
                "Vimeo" -> {
                    _state.update { it.copy(infoMsg = "Vimeo downloader coming soon!") }
                    null
                }
                else -> null
            }

            if (mediaInfo != null && mediaInfo.mediaUrl.isNotEmpty()) {
                _state.update { it.copy(infoMsg = "Found ${mediaInfo.type}! Starting download...") }
                startMediaDownload(context, mediaInfo)
            } else {
                val failureMessage = when (selectedPlatform) {
                    "Instagram", "YouTube", "Pinterest", "Facebook", "Twitter", "Reddit" ->
                        "Failed to extract media. The content may be private. Use 'VIEW' to log in and retry."
                    "LinkedIn", "Dailymotion", "Vimeo" ->
                        "Downloader for $selectedPlatform is coming soon!"
                    else -> "Failed to extract media from this URL."
                }
                _state.update { it.copy(infoMsg = failureMessage) }

                if (selectedPlatform !in listOf("LinkedIn", "Dailymotion", "Vimeo")) {
                    Toast.makeText(context, "Could not extract media from this URL", Toast.LENGTH_LONG).show()
                }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}