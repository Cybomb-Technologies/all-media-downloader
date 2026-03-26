@file:Suppress("DEPRECATION")

package com.cybomb.allmediadownloader.webviews

import android.content.Context
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.datamodels.WebViewResult
import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstagramWebViewScreen(
    url: String,
    instagramFetcher: InstagramFetcher,
    onFinished: (WebViewResult) -> Unit,
    startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
    getCookiesForUrl: (String) -> String? // Changed signature to nullable
) {
    val context = LocalContext.current
    val currentUrl by rememberUpdatedState(url)

    // State to hold the extracted media information - Refactored for state management encapsulation
    class InstagramWebViewScreenState(
        mediaInfo: DownloadMediaInfo? = null,
        isLoadingMedia: Boolean = false
    ) {
        var mediaInfo by mutableStateOf(mediaInfo)
        var isLoadingMedia by mutableStateOf(isLoadingMedia)
    }
    val screenState = remember { InstagramWebViewScreenState() }

    val scope = rememberCoroutineScope()

    // Function to run the media extraction logic
    val extractAndSetMediaInfo: (String) -> Unit = { pageUrl ->
        if (pageUrl.contains("instagram.com")) {
            screenState.isLoadingMedia = true
            scope.launch {
                // Use the cookies that the WebView automatically saves to CookieManager
                val cookies = getCookiesForUrl(pageUrl)

                // Use the fetcher to try and get the media details
                screenState.mediaInfo = instagramFetcher.fetchMediaDetails(pageUrl, cookies)

                if (screenState.mediaInfo == null || screenState.mediaInfo?.mediaUrl.isNullOrEmpty()) {
                    Toast.makeText(context, "Media link not found on this page.", Toast.LENGTH_SHORT).show()
                }
                screenState.isLoadingMedia = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Instagram Post Viewer") },
                navigationIcon = {
                    IconButton(onClick = { onFinished(WebViewResult(false, currentUrl)) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // WebView takes up most of the space
            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                factory = {
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Trigger media extraction when a page load is complete
                                url?.let { extractAndSetMediaInfo(it) }
                            }

                            // Essential for the WebView to handle redirects for Instagram
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                view?.loadUrl(url!!)
                                return true
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true

                        // Set the initial URL
                        loadUrl(currentUrl)
                    }
                }
            )

            // Download Button Section
            screenState.mediaInfo?.let { info ->
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        // CALL THE INJECTED UNIVERSAL DOWNLOAD FUNCTION
                        startMediaDownload(context, info)
                        onFinished(WebViewResult(true, info.postUrl)) // Go back after starting download
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    enabled = !screenState.isLoadingMedia
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("DOWNLOAD ${info.type.uppercase()}")
                    }
                }
            } ?: run {
                // Show loading or guidance text if mediaInfo is null
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (screenState.isLoadingMedia) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            "Navigate the post in the viewer above to find the media link.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
