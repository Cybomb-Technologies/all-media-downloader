package com.cybomb.allmediadownloader.navigation

import android.annotation.SuppressLint
import android.app.Activity // NEW IMPORT
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cybomb.allmediadownloader.SettingsScreen
import com.cybomb.allmediadownloader.datamodels.DownloaderScreen
import com.cybomb.allmediadownloader.datamodels.DownloaderViewModel
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.screens.DownloaderScreen
import com.cybomb.allmediadownloader.datamodels.Screen
import com.cybomb.allmediadownloader.fetcher.FacebookFetcher
import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
import com.cybomb.allmediadownloader.fetcher.PinterestFetcher
import com.cybomb.allmediadownloader.fetcher.RedditFetcher
import com.cybomb.allmediadownloader.fetcher.TwitterFetcher
import com.cybomb.allmediadownloader.fetcher.YouTubeFetcher
import com.cybomb.allmediadownloader.screens.GalleryScreen
import com.cybomb.allmediadownloader.screens.MainScreen
import com.cybomb.allmediadownloader.viewmodels.DownloaderViewModelFactory
import com.cybomb.allmediadownloader.viewmodels.startMediaDownload
import com.google.androidgamesdk.gametextinput.Settings

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppNavigation(
    navController: NavController,
    downloadedFiles: SnapshotStateList<DownloadMediaInfo>,
    activity: Activity // <-- NEW: Accept Activity from MainActivity
) {
    val context = LocalContext.current

    // Initialize fetchers and other dependencies (assuming this logic is correct)
    val instagramFetcher = remember { InstagramFetcher() }
    val youTubeFetcher = remember { YouTubeFetcher() }
    val pinterestFetcher = remember { PinterestFetcher() }
    val facebookFetcher = remember { FacebookFetcher() }
    val redditFetcher = remember { RedditFetcher() }
    val twitterFetcher = remember { TwitterFetcher() }

    val getCookiesForUrl: (String) -> String? = { link -> // Changed signature to nullable for consistency
     //   Log.d("AppNavigation", "Getting cookies for $link")
        null
    }


    val startMediaDownload: (Context, DownloadMediaInfo) -> Unit = { context, mediaInfo ->
        //startMediaDownload(context, mediaInfo)
        startMediaDownload(context, mediaInfo, downloadedFiles)
    }

    // Instantiate the ViewModel Factory
    val downloaderViewModelFactory = remember {
        DownloaderViewModelFactory(
            instagramFetcher,
            youTubeFetcher,
            pinterestFetcher,
            facebookFetcher,
            redditFetcher,
            twitterFetcher,
            getCookiesForUrl,
            startMediaDownload
        )
    }
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Gallery,
      //  Screen.Settings
    )

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute in bottomNavItems.map { it.route }) {
                BottomNavigationBar(navController, bottomNavItems)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController as androidx.navigation.NavHostController,
            startDestination = Screen.Home.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) { MainScreen(navController) }
            composable(Screen.Gallery.route) { GalleryScreen(downloadedFiles = downloadedFiles) }
           // composable(Screen.Settings.route) { SettingsScreen() }

            composable("downloader/{platform}") { backStackEntry ->
                DownloaderScreen(
                    viewModel = viewModel(factory = downloaderViewModelFactory),
                    selectedPlatformArg = backStackEntry.arguments?.getString("platform") ?: "Instagram",
                    navController = navController,
                    activity = activity, // <-- PASS ACTIVITY HERE
                    instagramFetcher = instagramFetcher,
                    youTubeFetcher = youTubeFetcher,
                    pinterestFetcher = pinterestFetcher,
                    facebookFetcher = facebookFetcher,
                    redditFetcher = redditFetcher,
                    twitterFetcher = twitterFetcher,
                    getCookiesForUrl = getCookiesForUrl,
                    startMediaDownload = startMediaDownload
                )
            }
        }
    }
}

//package com.cybomb.allmediadownloader.navigation
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.util.Log
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.snapshots.SnapshotStateList
//import androidx.compose.ui.platform.LocalContext
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
//import com.cybomb.allmediadownloader.screens.DownloaderScreen
//import com.cybomb.allmediadownloader.datamodels.Screen
//import com.cybomb.allmediadownloader.fetcher.FacebookFetcher
//import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
//import com.cybomb.allmediadownloader.fetcher.PinterestFetcher
//import com.cybomb.allmediadownloader.fetcher.RedditFetcher
//import com.cybomb.allmediadownloader.fetcher.TwitterFetcher
//import com.cybomb.allmediadownloader.fetcher.YouTubeFetcher
//import com.cybomb.allmediadownloader.screens.GalleryScreen
//import com.cybomb.allmediadownloader.screens.MainScreen
//import com.cybomb.allmediadownloader.viewmodels.DownloaderViewModelFactory
//import com.cybomb.allmediadownloader.viewmodels.startMediaDownload
//import kotlin.collections.contains
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun AppNavigation(navController: NavController, downloadedFiles: SnapshotStateList<DownloadMediaInfo>) {
//    // Initialize all fetchers
//    val instagramFetcher = remember { InstagramFetcher() }
//    val youTubeFetcher = remember { YouTubeFetcher() }
//    val pinterestFetcher = remember { PinterestFetcher() }
//    val facebookFetcher = remember { FacebookFetcher() }
//    val redditFetcher = remember { RedditFetcher() }
//    val twitterFetcher = remember { TwitterFetcher() }
//
//    val getCookiesForUrl: (String) -> String? = { link -> // Changed signature to nullable for consistency
//     //   Log.d("AppNavigation", "Getting cookies for $link")
//        null
//    }
//
//
//    val startMediaDownload: (Context, DownloadMediaInfo) -> Unit = { context, mediaInfo ->
//        //startMediaDownload(context, mediaInfo)
//        startMediaDownload(context, mediaInfo, downloadedFiles)
//    }
//
//    // Instantiate the ViewModel Factory
//    val downloaderViewModelFactory = remember {
//        DownloaderViewModelFactory(
//            instagramFetcher,
//            youTubeFetcher,
//            pinterestFetcher,
//            facebookFetcher,
//            redditFetcher,
//            twitterFetcher,
//            getCookiesForUrl,
//            startMediaDownload
//        )
//    }
//
//    val context = LocalContext.current
//    val bottomNavItems = listOf(
//        Screen.Home,
//        Screen.Gallery,
//        //Screen.Settings
//    )
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            if (currentRoute in bottomNavItems.map { it.route } || currentRoute?.startsWith("downloader") == true) {
//                BottomNavigationBar(navController, bottomNavItems)
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController as androidx.navigation.NavHostController,
//            startDestination = Screen.Home.route
//        ) {
//            composable(Screen.Home.route) { MainScreen(navController) }
//            composable(Screen.Gallery.route) { GalleryScreen(downloadedFiles = downloadedFiles) }
//            composable("downloader/{platform}") { backStackEntry ->
//
//                val platformArg = backStackEntry.arguments?.getString("platform") ?: "Instagram"
//
//                DownloaderScreen(
//                    // Inject ViewModel using the factory
//                    viewModel = viewModel(factory = downloaderViewModelFactory),
//
//                    // Pass dependencies used by webview screens/other functions directly
//                    instagramFetcher = instagramFetcher,
//                    youTubeFetcher = youTubeFetcher,
//                    pinterestFetcher = pinterestFetcher,
//                    facebookFetcher = facebookFetcher,
//                    redditFetcher = redditFetcher,
//                    twitterFetcher = twitterFetcher,
//                    getCookiesForUrl = getCookiesForUrl,
//                    //startMediaDownload = startMediaDownload,
//                    startMediaDownload = { ctx, info ->
//                        startMediaDownload(
//                            ctx,
//                            info,
//                            downloadedFiles
//                        )
//                    },
//                    selectedPlatformArg = platformArg,
//                    navController = navController
//                )
//            }
//        }
//    }
//}

//@Composable
//fun AppNavigation(navController: NavController, downloadedFiles: SnapshotStateList<DownloadMediaInfo>) {
//    // Initialize all fetchers
//    val instagramFetcher = remember { InstagramFetcher() }
//    val youTubeFetcher = remember { YouTubeFetcher() }
//    val pinterestFetcher = remember { PinterestFetcher() }
//    val facebookFetcher = remember { FacebookFetcher() }
//    val redditFetcher = remember { RedditFetcher() }
//    val twitterFetcher = remember { TwitterFetcher() }
//
//    val getCookiesForUrl: (String) -> String? = { link -> // Changed signature to nullable for consistency
//        Log.d("AppNavigation", "Getting cookies for $link")
//        null
//    }
//
//    val startMediaDownload: (Context, DownloadMediaInfo) -> Unit = { context, mediaInfo ->
//        startMediaDownload(context, mediaInfo)
//    }
//
//    // Instantiate the ViewModel Factory
//    val downloaderViewModelFactory = remember {
//        DownloaderViewModelFactory(
//            instagramFetcher,
//            youTubeFetcher,
//            pinterestFetcher,
//            facebookFetcher,
//            redditFetcher,
//            twitterFetcher,
//            getCookiesForUrl,
//            startMediaDownload
//        )
//    }
//
//    val context = LocalContext.current
//    val bottomNavItems = listOf(Screen.Home, Screen.Gallery, Screen.Settings)
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            if (currentRoute in bottomNavItems.map { it.route } || currentRoute?.startsWith("downloader") == true) {
//                BottomNavigationBar(navController, bottomNavItems)
//            }
//        }
//    ) { paddingValues ->
//        NavHost(
//            navController = navController as androidx.navigation.NavHostController,
//            startDestination = Screen.Home.route,
//            modifier = Modifier.padding(paddingValues)
//        ) {
//            composable(Screen.Home.route) { MainScreen(navController) }
//            composable(Screen.Gallery.route) { GalleryScreen(downloadedFiles = downloadedFiles) }
//            composable(Screen.Settings.route) { SettingsScreen() }
//
//            composable("downloader/{platform}") { backStackEntry ->
//                DownloaderScreen(
//                    // Inject ViewModel using the factory
//                    viewModel = viewModel(factory = downloaderViewModelFactory),
//
//                    // Pass dependencies used by webview screens/other functions directly
//                    instagramFetcher = instagramFetcher,
//                    youTubeFetcher = youTubeFetcher,
//                    pinterestFetcher = pinterestFetcher,
//                    facebookFetcher = facebookFetcher,
//                    redditFetcher = redditFetcher,
//                    twitterFetcher = twitterFetcher,
//                    getCookiesForUrl = getCookiesForUrl,
//                    startMediaDownload = startMediaDownload
//                )
//            }
//        }
//    }
//}