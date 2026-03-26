package com.cybomb.allmediadownloader.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cybomb.allmediadownloader.screens.components.DownloadContentList
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.BuildConfig

@Composable
fun GalleryScreen(downloadedFiles: SnapshotStateList<DownloadMediaInfo>) {
    val tabs = listOf("Images", "Videos", "All Downloads")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val bannerAdUnitId = BuildConfig.BANNER_AD_UNIT_ID_LIVE_TEST

    Column(modifier = Modifier.fillMaxSize()) {

        PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { selectedTabIndex = index },
                text = { Text(title) }
            )
        }
    }
        // -----------------------------------------------------------

        Spacer(modifier = Modifier.height(4.dp))
        AdBannerView(bannerAdUnitId = bannerAdUnitId) // Assuming this function is defined elsewhere

        when (selectedTabIndex) {
            0 -> DownloadContentList(contentType = "Image", downloadedFiles = downloadedFiles, context = context) // Assuming DownloadContentList is defined elsewhere
            1 -> DownloadContentList(contentType = "Video", downloadedFiles = downloadedFiles, context = context)
            2 -> DownloadContentList(contentType = "All Files", downloadedFiles = downloadedFiles, context = context)
        }
    }
}

//@Composable
//fun GalleryScreen(downloadedFiles: SnapshotStateList<DownloadMediaInfo>) {
//    val tabs = listOf("Images", "Videos", "All Downloads")
//    var selectedTabIndex by remember { mutableStateOf(0) }
//    val context = LocalContext.current
//    //val bannerAdUnitId = BANNER_AD_UNIT_ID // No need to define it locally
//    val bannerAdUnitId = BuildConfig.BANNER_AD_UNIT_ID_LIVE_TEST // No need to define it locally
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        TabRow(selectedTabIndex = selectedTabIndex) {
//            tabs.forEachIndexed { index, title ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index },
//                    text = { Text(title) }
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(4.dp))
//        AdBannerView(bannerAdUnitId = bannerAdUnitId)
//
//        when (selectedTabIndex) {
//            0 -> DownloadContentList(contentType = "Image", downloadedFiles = downloadedFiles, context = context)
//            1 -> DownloadContentList(contentType = "Video", downloadedFiles = downloadedFiles, context = context)
//            2 -> DownloadContentList(contentType = "All Files", downloadedFiles = downloadedFiles, context = context)
//        }
//    }
//}
