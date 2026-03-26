package com.cybomb.allmediadownloader.screens.admobs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cybomb.allmediadownloader.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay

@Composable
fun PersistentAdBanner(bannerAdUnitId: String) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var refreshCounter by remember { mutableStateOf(0) } // Force recomposition on refresh

    val adView = remember(refreshCounter) {
        AdView(context).apply {
            adUnitId = bannerAdUnitId
            setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360))

            adListener = object : com.google.android.gms.ads.AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(loadAdError: com.google.android.gms.ads.LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isAdLoaded = true // Set to true even on failure to show content
                }
            }
        }
    }

    // Auto-refresh every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(30000) // 5 seconds
            if (isAdLoaded) {
                isAdLoaded = false
                refreshCounter++ // This will trigger recreation of adView
            }
        }
    }

    // Load ad when adView changes
    LaunchedEffect(adView) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    // Show loading indicator while ad is loading
    if (!isAdLoaded) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), // Match your ad height
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    } else {
        AndroidView(
            factory = { adView },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
