package com.cybomb.allmediadownloader.datamodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.cybomb.allmediadownloader.R

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Download", Icons.Default.CloudDownload)
    data object Gallery : Screen("gallery", "My Files", Icons.Default.Folder)
    //data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

sealed class DownloaderScreen(
    val route: String,
    val label: String,
    val iconRes: Int,
    val color: Color
) {
   // data object Whatsapp : DownloaderScreen("downloader/whatsapp", "WhatsApp", R.drawable.ic_whatsapp, Color(0xFF25D366))
    data object Instagram : DownloaderScreen("downloader/instagram", "Instagram", R.drawable.ic_instagram, Color(0xFFC13584))
    //data object TikTok : DownloaderScreen("downloader/tiktok", "TikTok", R.drawable.ic_tiktok, Color(0xFF000000))
    data object Facebook : DownloaderScreen("downloader/facebook", "Facebook", R.drawable.ic_facebook, Color(0xFF1877F2))
    data object YouTube : DownloaderScreen("downloader/youtube", "YouTube", R.drawable.ic_youtube, Color(0xFFFF0000))
    data object Twitter : DownloaderScreen("downloader/twitter", "Twitter/X", R.drawable.ic_twitter, Color(0xFF1DA1F2))
    data object Snapchat : DownloaderScreen("downloader/snapchat", "Snapchat", R.drawable.ic_snapchat, Color(0xFFFFFF00))
    data object Pinterest : DownloaderScreen("downloader/pinterest", "Pinterest", R.drawable.ic_pinterest, Color(0xFFE60023))
    data object Reddit : DownloaderScreen("downloader/reddit", "Reddit", R.drawable.ic_reddit, Color(0xFFFF5700))
    data object LinkedIn : DownloaderScreen("downloader/linkedin", "LinkedIn", R.drawable.ic_linkedin, Color(0xFF0077B5))
    data object Dailymotion : DownloaderScreen("downloader/dailymotion", "Dailymotion", R.drawable.ic_dailymotion, Color(0xFF0066DC))
    data object Vimeo : DownloaderScreen("downloader/vimeo", "Vimeo", R.drawable.ic_vimeo, Color(0xFF1AB7EA))
}

//sealed class DownloaderScreen(val route: String, val label: String, val icon: ImageVector, val color: Color) {
//    data object Whatsapp : DownloaderScreen("downloader/whatsapp", "WhatsApp", Icons.Default.Chat, Color(0xFF25D366))
//    data object Instagram : DownloaderScreen("downloader/instagram", "Instagram", Icons.Default.CameraAlt, Color(0xFFC13584))
//    data object TikTok : DownloaderScreen("downloader/tiktok", "TikTok", Icons.Default.MusicNote, Color(0xFF000000))
//    data object Facebook : DownloaderScreen("downloader/facebook", "Facebook", Icons.Default.Facebook, Color(0xFF1877F2))
//    data object YouTube : DownloaderScreen("downloader/youtube", "YouTube", Icons.Default.PlayArrow, Color(0xFFFF0000))
//    data object Twitter : DownloaderScreen("downloader/twitter", "Twitter/X", Icons.Default.Tag, Color(0xFF1DA1F2))
//    data object Snapchat : DownloaderScreen("downloader/snapchat", "Snapchat", Icons.Default.FlashOn, Color(0xFFFFF000))
//
//    // NEW PLATFORMS
//    data object Pinterest : DownloaderScreen("downloader/pinterest", "Pinterest", Icons.Default.PushPin, Color(0xFFE60023))
//    data object Reddit : DownloaderScreen("downloader/reddit", "Reddit", Icons.Default.Forum, Color(0xFFFF5700))
//    data object LinkedIn : DownloaderScreen("downloader/linkedin", "LinkedIn", Icons.Default.Business, Color(0xFF0077B5))
//    data object Dailymotion : DownloaderScreen("downloader/dailymotion", "Dailymotion", Icons.Default.PlayCircle, Color(0xFF0066DC))
//    data object Vimeo : DownloaderScreen("downloader/vimeo", "Vimeo", Icons.Default.Videocam, Color(0xFF1AB7EA))
//}
//

val downloaderItems = listOf(
    //DownloaderScreen.Whatsapp,
    DownloaderScreen.Instagram,
    //DownloaderScreen.TikTok,
    DownloaderScreen.Facebook,
    DownloaderScreen.YouTube,
    //DownloaderScreen.Twitter,
    DownloaderScreen.Snapchat,
    DownloaderScreen.Pinterest,
    DownloaderScreen.Reddit,
    DownloaderScreen.LinkedIn,
    DownloaderScreen.Dailymotion,
    DownloaderScreen.Vimeo,
)