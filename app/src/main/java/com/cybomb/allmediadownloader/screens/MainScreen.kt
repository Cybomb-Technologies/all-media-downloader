package com.cybomb.allmediadownloader.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cybomb.allmediadownloader.R
import com.cybomb.allmediadownloader.datamodels.downloaderItems
import com.cybomb.allmediadownloader.screens.components.PlatformIcon
import com.cybomb.allmediadownloader.ui.theme.LightBlue
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.platform.LocalContext // Needed for Context
import androidx.compose.ui.viewinterop.AndroidView // Needed to host AdView
import com.google.android.gms.ads.AdRequest // Needed to load the ad
import com.cybomb.allmediadownloader.BuildConfig // <-- Make sure to import BuildConfig
import com.google.ads.mediation.admob.AdMobAdapter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val appTitle = "All Media Downloader" // Define the title for the App Bar
    val bannerAdUnitId = BuildConfig.BANNER_AD_UNIT_ID // No need to define it locally

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appTitle) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightBlue.copy(alpha = 0.2F)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold/TopAppBar
                .padding(horizontal = 12.dp) // Maintain horizontal padding
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            // --- 1. HOME BANNER IMAGE WITH GRADIENT (Title removed) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Set a fixed height for the banner
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // Background Image
                Image(
                    // Replace R.drawable.img_home_banner with your actual resource ID
                    painter = painterResource(id = R.drawable.img_home_banner),
                    contentDescription = "Home Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient Overlay for Readability (Bottom-to-Top) - KEPT
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f) // Darker at the bottom
                                ),
                                startY = 0f,
                                endY = 500f // Adjust endY to control gradient intensity
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(24.dp)) // Spacing between banner and grid

            // --- 2. PLATFORM ICONS GRID ---
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(downloaderItems.size) { index ->
                    val item = downloaderItems[index]
                    PlatformIcon(item) {
                        navController.navigate("downloader/${item.label}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
            AdBannerView(bannerAdUnitId = bannerAdUnitId)
        }
    }
}

// --- NEW COMPOSABLE FOR THE AD BANNER ---
@Composable
fun AdBannerView(bannerAdUnitId: String) {
    val context = LocalContext.current

    // Use AndroidView to embed the AdView (a traditional Android View) into Compose
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = {
            // Create and configure the AdView instance
            val adView = AdView(context).apply {
                adUnitId = bannerAdUnitId
                // Set the adaptive banner ad size
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, 360))
            }

            // Load the ad
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)

            adView // Return the AdView to be displayed
        }
    )
}

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun AdBannerViewCollab(bannerAdUnitId: String) {
    val context = LocalContext.current
    // Casting context to Activity is necessary to get correct display metrics for AdSize
    val activity = context as Activity

    // 1. Calculate the adaptive ad size (requires FULL_WIDTH for collapsible)
    val adSize = remember(context.resources.configuration.orientation) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, AdSize.FULL_WIDTH)
    }

    AndroidView(
        // The ad should fill the width of the bottom bar
        modifier = Modifier.fillMaxWidth(),
        factory = {
            AdView(context).apply {
                this.adUnitId = bannerAdUnitId
                this.setAdSize(adSize)

                // 2. Configure the AdRequest for the Collapsible Banner Feature.
                val extras = Bundle()
                // Set "bottom" to collapse toward the bottom of the screen.
                // Use "top" if you were placing it in the TopAppBar.
                extras.putString("collapsible", "bottom")

                val adRequest = AdRequest.Builder()
                    // This is required to pass the network extras to AdMob
                    .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                    .build()

                // 3. Load the ad
                this.loadAd(adRequest)
            }
        }
    )
}

//@Composable
//fun MainScreen(navController: NavController) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(12.dp)
//    ) {
//        // --- 1. HOME BANNER IMAGE WITH GRADIENT AND TITLE ---
//        // Assuming you want to display the first platform's name for simplicity,
//        // or a generic "All Media Downloader" title. We'll use a placeholder title.
//        val bannerTitle = "All Media Downloader" // You can customize this title
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(180.dp) // Set a fixed height for the banner
//                .clip(RoundedCornerShape(12.dp))
//        ) {
//            // Background Image
//            Image(
//                // Replace R.drawable.img_home_banner with your actual resource ID
//                painter = painterResource(id = R.drawable.img_home_banner),
//                contentDescription = "Home Banner",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
//
//            // Gradient Overlay for Readability (Bottom-to-Top)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        Brush.verticalGradient(
//                            colors = listOf(
//                                Color.Transparent,
//                                Color.Black.copy(alpha = 0.6f) // Darker at the bottom
//                            ),
//                            startY = 0f,
//                            endY = 500f // Adjust endY to control gradient intensity
//                        )
//                    )
//            )
//
//            // Title at the Bottom (Inside the Box)
//            Text(
//                text = bannerTitle,
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//                style = MaterialTheme.typography.headlineMedium,
//                modifier = Modifier
//                    .align(Alignment.BottomStart) // Aligns text to bottom-left
//                    .padding(16.dp) // Padding from the edge of the image
//            )
//        }
//
//        Spacer(modifier = Modifier.height(24.dp)) // Spacing between banner and grid
//
//        // --- 2. PLATFORM ICONS GRID ---
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(3),
//            contentPadding = PaddingValues(8.dp),
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//            horizontalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(downloaderItems.size) { index ->
//                val item = downloaderItems[index]
//                PlatformIcon(item) {
//                    navController.navigate("downloader/${item.label}")
//                }
//            }
//        }
//    }
//}

