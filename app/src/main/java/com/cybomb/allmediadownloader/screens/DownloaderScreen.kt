package com.cybomb.allmediadownloader.screens

import android.app.Activity
import android.content.Context
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cybomb.allmediadownloader.BuildConfig
import com.cybomb.allmediadownloader.BuildConfig.BANNER_AD_UNIT_ID
import com.cybomb.allmediadownloader.datamodels.DownloaderViewModel
import com.cybomb.allmediadownloader.datamodels.downloaderItems
import com.cybomb.allmediadownloader.fetcher.FacebookFetcher
import com.cybomb.allmediadownloader.fetcher.InstagramFetcher
import com.cybomb.allmediadownloader.fetcher.PinterestFetcher
import com.cybomb.allmediadownloader.fetcher.RedditFetcher
import com.cybomb.allmediadownloader.fetcher.TwitterFetcher
import com.cybomb.allmediadownloader.fetcher.YouTubeFetcher
import com.cybomb.allmediadownloader.ui.theme.PrimaryBlue
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.viewmodels.getPlatformInfo
import com.cybomb.allmediadownloader.webviews.DailymotionWebViewScreen
import com.cybomb.allmediadownloader.webviews.FacebookWebViewScreen
import com.cybomb.allmediadownloader.webviews.InstagramWebViewScreen
import com.cybomb.allmediadownloader.webviews.LinkedInWebViewScreen
import com.cybomb.allmediadownloader.webviews.PinterestWebViewScreen
import com.cybomb.allmediadownloader.webviews.RedditWebViewScreen
import com.cybomb.allmediadownloader.webviews.TwitterWebViewScreen
import com.cybomb.allmediadownloader.webviews.VimeoWebViewScreen
import com.cybomb.allmediadownloader.webviews.YouTubeWebViewScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderScreen(
    instagramFetcher: InstagramFetcher,
    youTubeFetcher: YouTubeFetcher,
    pinterestFetcher: PinterestFetcher,
    facebookFetcher: FacebookFetcher,
    redditFetcher: RedditFetcher,
    twitterFetcher: TwitterFetcher,
    getCookiesForUrl: (String) -> String?,
    startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
    viewModel: DownloaderViewModel = viewModel(),
    selectedPlatformArg: String = "Instagram",
    navController: NavController,
) {
    val context = LocalContext.current

    LaunchedEffect(selectedPlatformArg) {
        viewModel.selectPlatform(selectedPlatformArg)
    }

    val bannerAdUnitId = BANNER_AD_UNIT_ID // No need to define it locally

    // Collect state from ViewModel
    val screenState by viewModel.state.collectAsState()

    val url = screenState.url
    val isLoading = screenState.isLoading
    val infoMsg = screenState.infoMsg
    val selectedPlatform = screenState.selectedPlatform
    val showWebView = screenState.showWebView

    val scrollState = rememberScrollState()

    // Platforms that support WebView login
    val webViewSupportedPlatforms = listOf("Instagram", "YouTube", "Pinterest", "Facebook", "Twitter", "Reddit", "LinkedIn", "Dailymotion", "Vimeo")

    // Get platform details dynamically
    val platformItem = downloaderItems.find { it.label.equals(selectedPlatform, ignoreCase = true) }

    val platformColor = platformItem?.color ?: MaterialTheme.colorScheme.primary
    val platformIconRes = platformItem?.iconRes ?: 0
    MaterialTheme.colorScheme.primary



    // --- NEW: Inline Logic for Platform Color and Icons (replaces getPlatformInfo) ---
//    val platformColor = when (selectedPlatform) {
//        "Instagram" -> Color(0xFFC13584)
//        "Facebook" -> Color(0xFF1877F2)
//        "YouTube" -> Color(0xFFFF0000)
//        "Twitter" -> Color(0xFF1DA1F2)
//        "Pinterest" -> Color(0xFFE60023)
//        "Reddit" -> Color(0xFFFF5700)
//        "LinkedIn" -> Color(0xFF0077B5)
//        "Dailymotion" -> Color(0xFF0066DC)
//        "Vimeo" -> Color(0xFF1AB7EA)
//        else -> MaterialTheme.colorScheme.primary
//    }
//
//    // NOTE: This assumes you have R.drawable.ic_{platform_name} defined in your project.
//    // Replace '0' with the actual resource ID if needed.
//    val platformIconRes = when (selectedPlatform) {
//        "Instagram" -> 0
//        "Facebook" -> 0
//        "YouTube" -> 0
//        "Twitter" -> 0
//        "Pinterest" -> 0
//        "Reddit" -> 0
//        "LinkedIn" -> 0
//        "Dailymotion" -> 0
//        "Vimeo" -> 0
//        else -> 0
//    }

    // --- NEW: Inline Placeholder Text Logic (replaces getPlaceholderText) ---
    val urlPlaceholderText = when (selectedPlatform) {
        "Instagram" -> "e.g., https://www.instagram.com/reel/..."
        "YouTube" -> "e.g., https://www.youtube.com/watch?v=..."
        "Pinterest" -> "e.g., https://pin.it/... or https://www.pinterest.com/pin/..."
        "Facebook" -> "e.g., https://www.facebook.com/watch/?v=..."
        "Twitter" -> "e.g., https://twitter.com/.../status/..."
        "Reddit" -> "e.g., https://www.reddit.com/r/.../comments/..."
        "LinkedIn" -> "e.g., https://www.linkedin.com/posts/..."
        "Dailymotion" -> "e.g., https://www.dailymotion.com/video/..."
        "Vimeo" -> "e.g., https://vimeo.com/..."
        else -> "Paste URL"
    }

    // --- NEW: Inline Tip Text Logic ---
    val platformTipText = when (selectedPlatform) {
        "Instagram" -> "Use VIEW & LOGIN for private posts and stories."
        "YouTube" -> "Use VIEW & LOGIN for age-restricted or member-only videos."
        "Pinterest" -> "Use VIEW & LOGIN for accessing private pins."
        "Facebook" -> "Use VIEW & LOGIN to access private videos."
        "Twitter" -> "Use VIEW & LOGIN for protected tweets and private media."
        "Reddit" -> "Use VIEW & LOGIN for NSFW and content from private subreddits."
        "LinkedIn" -> "We are actively working on adding LinkedIn support soon!"
        "Dailymotion" -> "We are actively working on adding Dailymotion support soon!"
        "Vimeo" -> "We are actively working on adding Vimeo support soon!"
        else -> "Paste your media link above to start the download."
    }

    if (showWebView) {
        // --- Original WebView Logic (Unchanged) ---
        when (selectedPlatform) {
            "Instagram" -> InstagramWebViewScreen(
                url = url.text.trim(),
                instagramFetcher = instagramFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "YouTube" -> YouTubeWebViewScreen(
                url = url.text.trim(),
                youTubeFetcher = youTubeFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Pinterest" -> PinterestWebViewScreen(
                url = url.text.trim(),
                pinterestFetcher = pinterestFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Facebook" -> FacebookWebViewScreen(
                url = url.text.trim(),
                facebookFetcher = facebookFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Twitter" -> TwitterWebViewScreen(
                url = url.text.trim(),
                twitterFetcher = twitterFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Reddit" -> RedditWebViewScreen(
                url = url.text.trim(),
                redditFetcher = redditFetcher,
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "LinkedIn" -> LinkedInWebViewScreen(
                url = url.text.trim(),
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Dailymotion" -> DailymotionWebViewScreen(
                url = url.text.trim(),
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            "Vimeo" -> VimeoWebViewScreen(
                url = url.text.trim(),
                onFinished = { result ->
                    viewModel.onWebViewFinished(result)
                    if (result.success) {
                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
                    }
                },
                startMediaDownload = startMediaDownload,
                getCookiesForUrl = getCookiesForUrl
            )
            else -> viewModel.toggleWebView(false)
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("$selectedPlatform Downloader", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                // NEW: Increased horizontal padding for elegance
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // --- NEW: Inline Platform Icon Header (Replaces PlatformIconHeader) ---
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // NOTE: This assumes platformIconRes is a valid resource ID
                        if (platformIconRes != 0) {
                            Image(
                                painter = painterResource(id = platformIconRes),
                                contentDescription = selectedPlatform,
                                modifier = Modifier.size(80.dp)
                            )
                        } else {
                            // Fallback text if resource is not found/defined
                            Text(
                                text = selectedPlatform.first().toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = selectedPlatform,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { viewModel.updateUrl(it) },
                label = { Text("Paste $selectedPlatform Link") }, // Shorter, clearer label
                leadingIcon = { Icon(Icons.Filled.Link, contentDescription = "URL Link") },
                placeholder = {
                    Text(urlPlaceholderText) // NEW: Uses inline placeholder text
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // --- ACTION BUTTONS Group ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // PRIMARY DOWNLOAD BUTTON
                Button(
                    onClick = { viewModel.downloadMedia(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                ) {
                    // NEW: Animated content for loading state transition
                    AnimatedContent(
                        targetState = isLoading,
                        label = "download_button_content"
                    ) { loading ->
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 3.dp,
                                color = Color.White
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    when (selectedPlatform) {
                                        "LinkedIn", "Dailymotion", "Vimeo" -> "COMING SOON"
                                        else -> "DOWNLOAD MEDIA"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // VIEW BUTTON
                if (webViewSupportedPlatforms.contains(selectedPlatform)) {
                    OutlinedButton(
                        onClick = {
                            val link = url.text.trim()
                            if (link.isNotEmpty()) {
                                viewModel.toggleWebView(true)
                            } else {
                                Toast.makeText(context, "Please enter a valid URL first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && selectedPlatform !in listOf("LinkedIn", "Dailymotion", "Vimeo"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        // NEW: Outlined border uses platform color
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Visibility, contentDescription = "View in App", modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("VIEW & LOGIN", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } // End of Action Buttons Column

            // --- INFO/ERROR MESSAGE (with Animation) ---
            infoMsg?.let {
                Spacer(Modifier.height(24.dp))
                // NEW: Animated visibility for cleaner message appearance
                AnimatedVisibility(
                    visible = it.isNotEmpty(),
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Text(
                        text = it,
                        // NEW: Use error color for feedback/error messages
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // --- NEW: Inline Platform Tip Card (Replaces PlatformTipCard) ---
            Spacer(Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Tip",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = platformTipText, // Uses inline tip text
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
            AdBannerViewCollab(bannerAdUnitId = bannerAdUnitId)
        }
    }
}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DownloaderScreen(
//    instagramFetcher: InstagramFetcher,
//    youTubeFetcher: YouTubeFetcher,
//    pinterestFetcher: PinterestFetcher,
//    facebookFetcher: FacebookFetcher,
//    redditFetcher: RedditFetcher,
//    twitterFetcher: TwitterFetcher,
//    getCookiesForUrl: (String) -> String?,
//    startMediaDownload: (Context, DownloadMediaInfo) -> Unit,
//    viewModel: DownloaderViewModel = viewModel(),
//    selectedPlatformArg: String = "Instagram",
//    navController: NavController,
//) {
//    val context = LocalContext.current
//
//    LaunchedEffect(selectedPlatformArg) {
//        viewModel.selectPlatform(selectedPlatformArg)
//    }
//
//    // Collect state from ViewModel
//    val screenState by viewModel.state.collectAsState()
//
//    val url = screenState.url
//    val isLoading = screenState.isLoading
//    val infoMsg = screenState.infoMsg
//    val selectedPlatform = screenState.selectedPlatform
//    val showWebView = screenState.showWebView
//
//    val scrollState = rememberScrollState()
//
//    // Platforms that support WebView login
//    val webViewSupportedPlatforms = listOf("Instagram", "YouTube", "Pinterest", "Facebook", "Twitter", "Reddit", "LinkedIn", "Dailymotion", "Vimeo")
//
//    // Get platform details dynamically
//    val platformItem = downloaderItems.find { it.label.equals(selectedPlatform, ignoreCase = true) }
//
//    val platformColor = platformItem?.color ?: MaterialTheme.colorScheme.primary
//    val platformIconRes = platformItem?.iconRes ?: 0
//
//
//    // --- NEW: Inline Logic for Platform Color and Icons (replaces getPlatformInfo) ---
////    val platformColor = when (selectedPlatform) {
////        "Instagram" -> Color(0xFFC13584)
////        "Facebook" -> Color(0xFF1877F2)
////        "YouTube" -> Color(0xFFFF0000)
////        "Twitter" -> Color(0xFF1DA1F2)
////        "Pinterest" -> Color(0xFFE60023)
////        "Reddit" -> Color(0xFFFF5700)
////        "LinkedIn" -> Color(0xFF0077B5)
////        "Dailymotion" -> Color(0xFF0066DC)
////        "Vimeo" -> Color(0xFF1AB7EA)
////        else -> MaterialTheme.colorScheme.primary
////    }
////
////    // NOTE: This assumes you have R.drawable.ic_{platform_name} defined in your project.
////    // Replace '0' with the actual resource ID if needed.
////    val platformIconRes = when (selectedPlatform) {
////        "Instagram" -> 0
////        "Facebook" -> 0
////        "YouTube" -> 0
////        "Twitter" -> 0
////        "Pinterest" -> 0
////        "Reddit" -> 0
////        "LinkedIn" -> 0
////        "Dailymotion" -> 0
////        "Vimeo" -> 0
////        else -> 0
////    }
//
//    // --- NEW: Inline Placeholder Text Logic (replaces getPlaceholderText) ---
//    val urlPlaceholderText = when (selectedPlatform) {
//        "Instagram" -> "e.g., https://www.instagram.com/reel/..."
//        "YouTube" -> "e.g., https://www.youtube.com/watch?v=..."
//        "Pinterest" -> "e.g., https://pin.it/... or https://www.pinterest.com/pin/..."
//        "Facebook" -> "e.g., https://www.facebook.com/watch/?v=..."
//        "Twitter" -> "e.g., https://twitter.com/.../status/..."
//        "Reddit" -> "e.g., https://www.reddit.com/r/.../comments/..."
//        "LinkedIn" -> "e.g., https://www.linkedin.com/posts/..."
//        "Dailymotion" -> "e.g., https://www.dailymotion.com/video/..."
//        "Vimeo" -> "e.g., https://vimeo.com/..."
//        else -> "Paste URL"
//    }
//
//    // --- NEW: Inline Tip Text Logic ---
//    val platformTipText = when (selectedPlatform) {
//        "Instagram" -> "Use VIEW & LOGIN for private posts and stories."
//        "YouTube" -> "Use VIEW & LOGIN for age-restricted or member-only videos."
//        "Pinterest" -> "Use VIEW & LOGIN for accessing private pins."
//        "Facebook" -> "Use VIEW & LOGIN to access private videos."
//        "Twitter" -> "Use VIEW & LOGIN for protected tweets and private media."
//        "Reddit" -> "Use VIEW & LOGIN for NSFW and content from private subreddits."
//        "LinkedIn" -> "We are actively working on adding LinkedIn support soon!"
//        "Dailymotion" -> "We are actively working on adding Dailymotion support soon!"
//        "Vimeo" -> "We are actively working on adding Vimeo support soon!"
//        else -> "Paste your media link above to start the download."
//    }
//
//    if (showWebView) {
//        // --- Original WebView Logic (Unchanged) ---
//        when (selectedPlatform) {
//            "Instagram" -> InstagramWebViewScreen(
//                url = url.text.trim(),
//                instagramFetcher = instagramFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "YouTube" -> YouTubeWebViewScreen(
//                url = url.text.trim(),
//                youTubeFetcher = youTubeFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Pinterest" -> PinterestWebViewScreen(
//                url = url.text.trim(),
//                pinterestFetcher = pinterestFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Facebook" -> FacebookWebViewScreen(
//                url = url.text.trim(),
//                facebookFetcher = facebookFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Twitter" -> TwitterWebViewScreen(
//                url = url.text.trim(),
//                twitterFetcher = twitterFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Reddit" -> RedditWebViewScreen(
//                url = url.text.trim(),
//                redditFetcher = redditFetcher,
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "LinkedIn" -> LinkedInWebViewScreen(
//                url = url.text.trim(),
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Dailymotion" -> DailymotionWebViewScreen(
//                url = url.text.trim(),
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            "Vimeo" -> VimeoWebViewScreen(
//                url = url.text.trim(),
//                onFinished = { result ->
//                    viewModel.onWebViewFinished(result)
//                    if (result.success) {
//                        Toast.makeText(context, "Cookies updated. Try downloading again.", Toast.LENGTH_LONG).show()
//                    }
//                },
//                startMediaDownload = startMediaDownload,
//                getCookiesForUrl = getCookiesForUrl
//            )
//            else -> viewModel.toggleWebView(false)
//        }
//        return
//    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("$selectedPlatform Downloader", fontWeight = FontWeight.SemiBold) },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                // NEW: Use platform color for subtle Top Bar accent
//                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    titleContentColor = platformColor
//                )
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                // NEW: Increased horizontal padding for elegance
//                .padding(horizontal = 24.dp)
//                .verticalScroll(scrollState),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(Modifier.height(32.dp))
//
//            // --- NEW: Inline Platform Icon Header (Replaces PlatformIconHeader) ---
//            Box(
//                modifier = Modifier.size(80.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Card(
//                    modifier = Modifier.fillMaxSize(),
//                    shape = MaterialTheme.shapes.large,
//                    colors = CardDefaults.cardColors(
//                        containerColor = platformColor // Platform brand color
//                    ),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//                ) {
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        // NOTE: This assumes platformIconRes is a valid resource ID
//                        if (platformIconRes != 0) {
//                            Image(
//                                painter = painterResource(id = platformIconRes),
//                                contentDescription = selectedPlatform,
//                                modifier = Modifier.size(80.dp)
//                            )
//                        } else {
//                            // Fallback text if resource is not found/defined
//                            Text(
//                                text = selectedPlatform.first().toString(),
//                                style = MaterialTheme.typography.headlineLarge,
//                                color = Color.White
//                            )
//                        }
//                    }
//                }
//            }
//            Spacer(Modifier.height(16.dp))
//            Text(
//                text = selectedPlatform,
//                style = MaterialTheme.typography.headlineMedium,
//                fontWeight = FontWeight.ExtraBold,
//                color = platformColor
//            )
//
//            Spacer(Modifier.height(24.dp))
//
//            OutlinedTextField(
//                value = url,
//                onValueChange = { viewModel.updateUrl(it) },
//                label = { Text("Paste $selectedPlatform Link") }, // Shorter, clearer label
//                leadingIcon = { Icon(Icons.Filled.Link, contentDescription = "URL Link") },
//                placeholder = {
//                    Text(urlPlaceholderText) // NEW: Uses inline placeholder text
//                },
//                singleLine = true,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(24.dp))
//
//            // --- ACTION BUTTONS Group ---
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                // PRIMARY DOWNLOAD BUTTON
//                Button(
//                    onClick = { viewModel.downloadMedia(context) },
//                    modifier = Modifier.fillMaxWidth(),
//                    enabled = !isLoading,
//                    colors = ButtonDefaults.buttonColors(
//                        // NEW: Use platform color for primary action
//                        containerColor = platformColor,
//                        contentColor = Color.White,
//                        disabledContainerColor = platformColor.copy(alpha = 0.5f),
//                        disabledContentColor = Color.White.copy(alpha = 0.7f)
//                    )
//                ) {
//                    // NEW: Animated content for loading state transition
//                    AnimatedContent(
//                        targetState = isLoading,
//                        label = "download_button_content"
//                    ) { loading ->
//                        if (loading) {
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(24.dp),
//                                strokeWidth = 3.dp,
//                                color = Color.White
//                            )
//                        } else {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Icon(Icons.Filled.Download, contentDescription = "Download", modifier = Modifier.size(20.dp))
//                                Spacer(Modifier.width(8.dp))
//                                Text(
//                                    when (selectedPlatform) {
//                                        "LinkedIn", "Dailymotion", "Vimeo" -> "COMING SOON"
//                                        else -> "DOWNLOAD MEDIA"
//                                    },
//                                    fontWeight = FontWeight.Bold
//                                )
//                            }
//                        }
//                    }
//                }
//
//                // VIEW BUTTON
//                if (webViewSupportedPlatforms.contains(selectedPlatform)) {
//                    OutlinedButton(
//                        onClick = {
//                            val link = url.text.trim()
//                            if (link.isNotEmpty()) {
//                                viewModel.toggleWebView(true)
//                            } else {
//                                Toast.makeText(context, "Please enter a valid URL first", Toast.LENGTH_SHORT).show()
//                            }
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        enabled = !isLoading && selectedPlatform !in listOf("LinkedIn", "Dailymotion", "Vimeo"),
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            contentColor = MaterialTheme.colorScheme.onSurface,
//                        ),
//                        // NEW: Outlined border uses platform color
//                        border = BorderStroke(1.dp, platformColor.copy(alpha = 0.7f))
//                    ) {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Icon(Icons.Filled.Visibility, contentDescription = "View in App", modifier = Modifier.size(20.dp))
//                            Spacer(Modifier.width(8.dp))
//                            Text("VIEW & LOGIN", fontWeight = FontWeight.SemiBold)
//                        }
//                    }
//                }
//            } // End of Action Buttons Column
//
//            // --- INFO/ERROR MESSAGE (with Animation) ---
//            infoMsg?.let {
//                Spacer(Modifier.height(24.dp))
//                // NEW: Animated visibility for cleaner message appearance
//                AnimatedVisibility(
//                    visible = it.isNotEmpty(),
//                    enter = fadeIn() + slideInVertically(),
//                    exit = fadeOut() + slideOutVertically()
//                ) {
//                    Text(
//                        text = it,
//                        // NEW: Use error color for feedback/error messages
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.padding(horizontal = 8.dp),
//                        textAlign = TextAlign.Center,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//
//            // --- NEW: Inline Platform Tip Card (Replaces PlatformTipCard) ---
//            Spacer(Modifier.height(32.dp))
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
//                ),
//                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//            ) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Info,
//                        contentDescription = "Tip",
//                        modifier = Modifier
//                            .size(24.dp)
//                            .padding(end = 8.dp),
//                        tint = platformColor // Use platform color for the tip icon accent
//                    )
//                    Text(
//                        text = platformTipText, // Uses inline tip text
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant,
//                        textAlign = TextAlign.Start
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(48.dp))
//        }
//    }
//}
