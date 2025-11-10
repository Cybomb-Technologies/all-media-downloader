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
//fetcher
//datamodels
import com.cybomb.allmediadownloader.navigation.AppNavigation
import com.cybomb.allmediadownloader.navigation.RequestStoragePermission
import com.cybomb.allmediadownloader.screens.SplashScreen
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Your custom theme should go here
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF007bff), // A nice blue for the primary color
                secondary = Color(0xFF28a745) // Green for accents/download buttons
            )) {
                RequestStoragePermission()
                AllInOneDownloaderApp()
            }
        }
    }
}

@Composable
fun AllInOneDownloaderApp() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    // GLOBAL STATE: List of all files downloaded (simulated local storage)
    // Initialize with sample data to preview the GalleryScreen
    //val downloadedFiles = remember { mutableStateListOf<DownloadMediaInfo>().apply { addAll(getSampleDownloadedMedia()) } }
    val downloadedFiles = remember { mutableStateListOf<DownloadMediaInfo>() }

    // Use a LaunchedEffect to manage the splash screen duration
//    LaunchedEffect(Unit) {
//        delay(2000) // Show splash for 2 seconds
//        showSplash = false
//    }

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
//            AnimatedVisibility(
//                visible = showSplash,
//                enter = fadeIn(animationSpec = tween(500)),
//                exit = fadeOut(animationSpec = tween(500))
//            ) {
//                SplashScreen()
//            }

//            AnimatedVisibility(
//                visible = !showSplash,
//                enter = fadeIn(animationSpec = tween(500)),
//                exit = fadeOut(animationSpec = tween(500))
//            ) {
//                AppNavigation(navController, downloadedFiles)
//            }

            AppNavigation(navController, downloadedFiles)
        }
    }
}


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
        Divider()
        SettingsItem(Icons.Default.Star, "Rate Us") { /* TODO: Open play store */ }
        Divider()
        SettingsItem(Icons.Default.Security, "Privacy Policy") { /* TODO: Open webview */ }
        Divider()
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
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAllInOneDownloaderApp() {
    MaterialTheme {
        AllInOneDownloaderApp()
    }
}