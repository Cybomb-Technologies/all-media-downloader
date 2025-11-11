package com.cybomb.allmediadownloader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import com.cybomb.allmediadownloader.navigation.AppNavigation
import com.cybomb.allmediadownloader.navigation.RequestStoragePermission
import com.cybomb.allmediadownloader.screens.SplashScreen
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

// NEW AD MANAGER OBJECT
object AdManager {
    private var mInterstitialAd: InterstitialAd? = null
    private const val TAG = "AdManager"

    // Function to load the ad (call this in your initial ad setup)
    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            // Access the ad unit ID from BuildConfig
            BuildConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                   // Log.d(TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                   // Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    // Function to show the ad if it's ready
    fun showInterstitialAd(activity: ComponentActivity) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                   // Log.d(TAG, "Ad dismissed.")
                    mInterstitialAd = null
                    // Pre-load the next ad immediately after the current one is dismissed
                    loadInterstitialAd(activity.applicationContext)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    //Log.d(TAG, "Ad failed to show.")
                    mInterstitialAd = null
                    // Pre-load the next ad even if the show failed
                    loadInterstitialAd(activity.applicationContext)
                }

                override fun onAdShowedFullScreenContent() {
                   // Log.d(TAG, "Ad showed full-screen content.")
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            //Log.d(TAG, "The interstitial ad wasn't ready yet.")
            // Try to load a new one if it wasn't ready
            loadInterstitialAd(activity.applicationContext)
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ads first
        MobileAds.initialize(this) { initializationStatus ->
            // NEW: Load the first interstitial ad after MobileAds is initialized
            AdManager.loadInterstitialAd(applicationContext)

            // Ads are initialized, now set content
            setContent {
                MaterialTheme(colorScheme = lightColorScheme(
                    primary = Color(0xFF007bff),
                    secondary = Color(0xFF28a745)
                )) {
                    RequestStoragePermission()
                    // Pass this activity instance to the composable for ad showing
                    AllInOneDownloaderApp(this)
                }
            }
        }
    }
}


@Composable
fun AllInOneDownloaderApp(activity: ComponentActivity) { // Updated signature
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    // GLOBAL STATE: List of all files downloaded (simulated local storage)
    val downloadedFiles = remember { mutableStateListOf<DownloadMediaInfo>() }

    // Use a LaunchedEffect to show the interstitial ad when the MainScreen is the destination
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            if (backStackEntry.destination.route == "home") {
                // Show the ad every time the user navigates back to the Home screen
                AdManager.showInterstitialAd(activity)
            }
        }
    }

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Pass the activity down through AppNavigation
            AppNavigation(navController, downloadedFiles, activity)
        }
    }
}


@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Application Settings",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        SettingsItem(Icons.Default.Language, "Change Language") { /* TODO: Language dialog */ }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        SettingsItem(Icons.Default.Star, "Rate Us") { /* TODO: Open play store */ }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        SettingsItem(Icons.Default.Security, "Privacy Policy") { /* TODO: Open webview */ }
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        SettingsItem(Icons.Default.Info, "About Us") { /* Replaces AboutUsActivity.java */ }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Go")
    }
}
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize ads first
//        MobileAds.initialize(this) { initializationStatus ->
//            // Ads are initialized, now set content
//            setContent {
//                MaterialTheme(colorScheme = lightColorScheme(
//                    primary = Color(0xFF007bff),
//                    secondary = Color(0xFF28a745)
//                )) {
//                    RequestStoragePermission()
//                    AllInOneDownloaderApp()
//                }
//            }
//        }
//    }
//}

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        CoroutineScope(Dispatchers.IO).launch {
//            // Initialize the Google Mobile Ads SDK on a background thread.
//            MobileAds.initialize(this@MainActivity) {}
//        }
//
//        setContent {
//            // Your custom theme should go here
//            MaterialTheme(colorScheme = lightColorScheme(
//                primary = Color(0xFF007bff), // A nice blue for the primary color
//                secondary = Color(0xFF28a745) // Green for accents/download buttons
//            )) {
//                RequestStoragePermission()
//                AllInOneDownloaderApp()
//            }
//        }
//    }
//}

//@Composable
//fun AllInOneDownloaderApp() {
//    val navController = rememberNavController()
//    var showSplash by remember { mutableStateOf(true) }
//
//    // GLOBAL STATE: List of all files downloaded (simulated local storage)
//    // Initialize with sample data to preview the GalleryScreen
//    //val downloadedFiles = remember { mutableStateListOf<DownloadMediaInfo>().apply { addAll(getSampleDownloadedMedia()) } }
//    val downloadedFiles = remember { mutableStateListOf<DownloadMediaInfo>() }
//
//    // Use a LaunchedEffect to manage the splash screen duration
////    LaunchedEffect(Unit) {
////        delay(2000) // Show splash for 2 seconds
////        showSplash = false
////    }
//
//    Scaffold { padding ->
//        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
////            AnimatedVisibility(
////                visible = showSplash,
////                enter = fadeIn(animationSpec = tween(500)),
////                exit = fadeOut(animationSpec = tween(500))
////            ) {
////                SplashScreen()
////            }
//
////            AnimatedVisibility(
////                visible = !showSplash,
////                enter = fadeIn(animationSpec = tween(500)),
////                exit = fadeOut(animationSpec = tween(500))
////            ) {
////                AppNavigation(navController, downloadedFiles)
////            }
//
//            AppNavigation(navController, downloadedFiles)
//        }
//    }
//}


// =========================================================================================
// VIEWMODEL FACTORY FOR DEPENDENCY INJECTION
// =========================================================================================

//
//// --- Sample Data for Gallery Preview ---
//private fun getSampleDownloadedMedia(): List<DownloadMediaInfo> {
//    return listOf(
//        DownloadMediaInfo(
//            postUrl = "https://www.instagram.com/p/sample123/",
//            mediaUrl = "/storage/emulated/0/Download/Insta_vid_12345.mp4",
//            type = "Video",
//            size = 15400000, // 15.4 MB
//            fileName = "Insta_vid_12345.mp4",
//            downloadDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1),
//            platform = "Instagram"
//        ),
//        DownloadMediaInfo(
//            postUrl = "https://www.pinterest.com/pin/sampleimage/",
//            mediaUrl = "/storage/emulated/0/Download/Pin_image_9876.jpg",
//            type = "Image",
//            size = 2100000, // 2.1 MB
//            fileName = "Pin_image_9876.jpg",
//            downloadDate = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(5),
//            platform = "Pinterest"
//        ),
//        DownloadMediaInfo(
//            postUrl = "https://www.youtube.com/watch?v=samplevideo",
//            mediaUrl = "/storage/emulated/0/Download/YouTube_clip_abc.mp4",
//            type = "Video",
//            size = 50000000, // 50 MB
//            fileName = "YouTube_clip_abc.mp4",
//            downloadDate = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1),
//            platform = "YouTube"
//        ),
//        DownloadMediaInfo(
//            postUrl = "https://www.facebook.com/post/samplephoto",
//            mediaUrl = "/storage/emulated/0/Download/FB_photo_def.jpeg",
//            type = "Image",
//            size = 850000, // 850 KB
//            fileName = "FB_photo_def.jpeg",
//            downloadDate = System.currentTimeMillis(),
//            platform = "Facebook"
//        )
//    )
//}




// --- Navigation Bar Component ---

// --- Helper Functions ---

// Replaces MainActivity.java






//
//@Composable
//fun SettingsScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Application Settings",
//            style = MaterialTheme.typography.headlineSmall,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//        SettingsItem(Icons.Default.Language, "Change Language") { /* TODO: Language dialog */ }
//        Divider()
//        SettingsItem(Icons.Default.Star, "Rate Us") { /* TODO: Open play store */ }
//        Divider()
//        SettingsItem(Icons.Default.Security, "Privacy Policy") { /* TODO: Open webview */ }
//        Divider()
//        SettingsItem(Icons.Default.Info, "About Us") { /* Replaces AboutUsActivity.java */ }
//    }
//}
//
//@Composable
//fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(vertical = 16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
//        Spacer(modifier = Modifier.width(16.dp))
//        Text(title, style = MaterialTheme.typography.titleMedium)
//        Spacer(modifier = Modifier.weight(1f))
//        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go")
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewAllInOneDownloaderApp() {
//    MaterialTheme {
//        AllInOneDownloaderApp()
//    }
//}