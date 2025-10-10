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
import com.cybomb.allmediadownloader.datamodels.WebViewResult
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailymotionWebViewScreen(
    url: String,
    onFinished: (WebViewResult) -> Unit,
    startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
    getCookiesForUrl: (String) -> String?
) {
    val context = LocalContext.current
    val currentUrl by rememberUpdatedState(url)

    // State to hold the extracted media information - Refactored for state management encapsulation
    class DailymotionWebViewScreenState(
        mediaInfo: DownloadMediaInfo? = null,
        isLoadingMedia: Boolean = false
    ) {
        var mediaInfo by mutableStateOf(mediaInfo)
        var isLoadingMedia by mutableStateOf(isLoadingMedia)
    }
    val screenState = remember { DailymotionWebViewScreenState() }

    val scope = rememberCoroutineScope()

    // Placeholder function for media extraction, to match state pattern
    val extractAndSetMediaInfo: (String) -> Unit = { pageUrl ->
        if (pageUrl.contains("dailymotion.com")) {
            screenState.isLoadingMedia = true
            scope.launch {
                delay(1000) // Simulate fetching
                // Placeholder: Dailymotion fetcher not implemented
                Toast.makeText(context, "Dailymotion media extraction logic not yet implemented.", Toast.LENGTH_SHORT).show()
                screenState.isLoadingMedia = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dailymotion Video Viewer") },
                navigationIcon = {
                    IconButton(onClick = { onFinished(WebViewResult(false, currentUrl)) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                factory = {
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                url?.let { extractAndSetMediaInfo(it) }
                            }
                            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                view?.loadUrl(url!!)
                                return true
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        loadUrl(currentUrl)
                    }
                }
            )

            screenState.mediaInfo?.let { info ->
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        startMediaDownload(context, info)
                        onFinished(WebViewResult(true, info.postUrl))
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
                            "Navigate and log in to a Dailymotion video to extract media. Logic is pending implementation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
