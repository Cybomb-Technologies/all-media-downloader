package com.cybomb.allmediadownloader.viewmodels

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


fun startMediaDownload(
    context: Context,
    mediaInfo: DownloadMediaInfo,
    downloadedFiles: SnapshotStateList<DownloadMediaInfo>
) {
    val fileName = mediaInfo.fileName ?: "download_${System.currentTimeMillis()}.mp4"
    val request = DownloadManager.Request(Uri.parse(mediaInfo.mediaUrl))
        .setTitle(fileName)
        .setDescription("Downloading media...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)

    CoroutineScope(Dispatchers.IO).launch {
        var downloading = true
        while (downloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor != null && cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    val filePath = Uri.parse(uriString).path ?: ""
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    withContext(Dispatchers.Main) {
                        downloadedFiles.add(
                            mediaInfo.copy(
                                mediaUrl = filePath,
                                fileName = fileName,
                                size = size,
                                downloadDate = System.currentTimeMillis()
                            )
                        )
                        Toast.makeText(context, "Download completed: $fileName", Toast.LENGTH_SHORT).show()
                    }
                    downloading = false
                } else if (status == DownloadManager.STATUS_FAILED) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Download failed!", Toast.LENGTH_SHORT).show()
                    }
                    downloading = false
                }
            }
            cursor?.close()
            delay(1000)
        }
    }
}

//fun startMediaDownload(context: Context, mediaInfo: DownloadMediaInfo) {
//    Log.d("Download", "Simulating download start for: ${mediaInfo.fileName}")
//
//    try {
//        val extension = when {
//            mediaInfo.mediaUrl.contains(".mp4", ignoreCase = true) -> "mp4"
//            mediaInfo.mediaUrl.contains(".jpg", ignoreCase = true) -> "jpg"
//            mediaInfo.mediaUrl.contains(".jpeg", ignoreCase = true) -> "jpeg"
//            mediaInfo.mediaUrl.contains(".png", ignoreCase = true) -> "png"
//            mediaInfo.type == "Video" -> "mp4" // Default video extension
//            mediaInfo.type.contains("Image") || mediaInfo.type.contains("Thumbnail") -> "jpg" // Default image/thumbnail
//            else -> "dat" // Fallback for unknown streams
//        }
//
//        val platform = if (mediaInfo.postUrl.contains("youtube", ignoreCase = true)) "youtube" else "instagram"
//        val fileName = "${platform}_${mediaInfo.type.lowercase().replace(" ", "")}_${System.currentTimeMillis()}.$extension"
//
//        val request = DownloadManager.Request(Uri.parse(mediaInfo.mediaUrl))
//            .setTitle("$platform ${mediaInfo.type}")
//            .setDescription(fileName)
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            .addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
//            .addRequestHeader("Referer", mediaInfo.postUrl) // Use the original URL as referer
//            .addRequestHeader("Accept", "*/*")
//            .setAllowedOverMetered(true)
//            .setAllowedOverRoaming(true)
//            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
//
//        // ⭐️ IMPORTANT: Add cookies to the DownloadManager request if available
//        // Note: For YouTube, the raw media URL often doesn't need the cookie,
//        // but if the URL is a signed link, the initial request might need it.
//        val cookies = getCookiesForUrl(mediaInfo.postUrl)
//        cookies?.let {
//            request.addRequestHeader("Cookie", it)
//        }
//
//
//        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val downloadId = downloadManager.enqueue(request)
//
//        monitorMediaDownload(context, downloadId, fileName, platform)
//
//        Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_LONG).show()
//
//    } catch (e: Exception) {
//        Log.e("MediaDownloader", "Download failed", e)
//        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
//    }
//}

/**
 * Monitors the DownloadManager status for the given ID.
 */
private fun monitorMediaDownload(context: Context, downloadId: Long, fileName: String, platform: String) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    CoroutineScope(Dispatchers.IO).launch {
        var isDownloading = true
        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "$platform media downloaded: $fileName", Toast.LENGTH_SHORT).show()
                        }
                        isDownloading = false
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val errorMsg = when (reason) {
                            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP error - source may have blocked the request"
                            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
                            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Server rejected request"
                            DownloadManager.ERROR_FILE_ERROR -> "File error"
                            else -> "Download failed (code: $reason)"
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "$platform download failed: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                        isDownloading = false
                    }
                    DownloadManager.STATUS_RUNNING -> {
                       // Log.d("MediaDownloader", "Download running: $fileName")
                    }
                }
            }
            cursor.close()
            delay(2000)
        }
    }
}

// REMOVED redundant startInstagramDownload/monitorInstagramDownload functions,
// as the new universal startMediaDownload handles both platforms.

// Advanced Instagram media extractor that tries multiple methods
suspend fun extractInstagramMediaUrlAdvanced(postUrl: String): String? = withContext(Dispatchers.IO) {
    try {
      //  Log.d("Instagram", "Extracting from: $postUrl")

        // Method 1: Try different user agents and headers
        val userAgents = listOf(
            "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
        )

        for (userAgent in userAgents) {
            val mediaUrl = tryExtractWithUserAgent(postUrl, userAgent)
            if (mediaUrl != null) return@withContext mediaUrl
            delay(1000) // Rate limiting
        }

        // Method 2: Try embedding approach
        val embedUrl = postUrl.replace("/reel/", "/p/").replace("/p/", "/p/") + "embed/"
        val embedResult = tryExtractFromEmbed(embedUrl)
        if (embedResult != null) return@withContext embedResult

        return@withContext null

    } catch (e: Exception) {
        Log.e("Instagram", "Error extracting media", e)
        null
    }
}

// Try extraction with specific user agent
private suspend fun tryExtractWithUserAgent(url: String, userAgent: String): String? {
    return try {
        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", userAgent)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.5")
            .header("Accept-Encoding", "gzip, deflate")
            .header("Connection", "keep-alive")
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) return null

        val html = response.body?.string() ?: return null
       // Log.d("Instagram", "HTML length: ${html.length}")

        // Multiple extraction patterns
        extractMediaFromHtml(html)

    } catch (e: Exception) {
        Log.e("Instagram", "Error with user agent $userAgent", e)
        null
    }
}

// Try extraction from embed version
private suspend fun tryExtractFromEmbed(embedUrl: String): String? {
    return try {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(embedUrl)
            .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X)")
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val html = response.body?.string()
            html?.let { extractMediaFromHtml(it) }
        } else null

    } catch (e: Exception) {
        Log.e("Instagram", "Error extracting from embed", e)
        null
    }
}

// Extract media URLs from HTML using multiple patterns
private fun extractMediaFromHtml(html: String): String? {
    // Pattern 1: Look for video_url in JavaScript
    val videoPattern1 = Pattern.compile("\"video_url\":\"([^\"]+)\"")
    val matcher1 = videoPattern1.matcher(html)
    if (matcher1.find()) {
        val videoUrl = matcher1.group(1)?.replace("\\u0026", "&")?.replace("\\/", "/")
        if (videoUrl?.contains(".mp4") == true) return videoUrl
    }

    // Pattern 2: Look for video sources in script tags
    val videoPattern2 = Pattern.compile("\"src\":\"([^\"]+\\.mp4[^\"]*)")
    val matcher2 = videoPattern2.matcher(html)
    if (matcher2.find()) {
        val videoUrl = matcher2.group(1)?.replace("\\u0026", "&")?.replace("\\/", "/")
        return videoUrl
    }

    // Pattern 3: Look for display_url (for images)
    val imagePattern = Pattern.compile("\"display_url\":\"([^\"]+)")
    val imageMatcher = imagePattern.matcher(html)
    if (imageMatcher.find()) {
        val imageUrl = imageMatcher.group(1)?.replace("\\u0026", "&")?.replace("\\/", "/")
        return imageUrl
    }

    // Pattern 4: Traditional og:video meta tags
    val doc = Jsoup.parse(html)
    val videoUrl = doc.select("meta[property=og:video]").attr("content")
    if (videoUrl.isNotEmpty()) return videoUrl

    val imageUrl = doc.select("meta[property=og:image]").attr("content")
    if (imageUrl.isNotEmpty()) return imageUrl

    return null
}

// Monitor download progress
private fun monitorDownload(context: Context, downloadId: Long, fileName: String) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    CoroutineScope(Dispatchers.IO).launch {
        var isDownloading = true
        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val reason = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))

                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Download completed: $fileName", Toast.LENGTH_SHORT).show()
                        }
                        isDownloading = false
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val errorMsg = when (reason) {
                            DownloadManager.ERROR_HTTP_DATA_ERROR -> "HTTP data error - media may be protected"
                            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirects"
                            DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "HTTP error - server rejected request"
                            else -> "Download failed (code: $reason)"
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Download failed: $errorMsg", Toast.LENGTH_LONG).show()
                        }
                        isDownloading = false
                    }
                }
            }
            cursor.close()
            delay(2000)
        }
    }
}


// Check if URL is a valid media URL
private fun isValidMediaUrl(url: String): Boolean {
    return url.contains(".mp4") || url.contains(".jpg") ||
            url.contains(".jpeg") || url.contains(".png") ||
            url.contains("video") || url.contains("scontent")
}

// Advanced download with better headers
fun startAdvancedDownload(context: Context, mediaUrl: String, originalUrl: String) {
    try {
        val extension = when {
            mediaUrl.contains(".mp4") -> "mp4"
            mediaUrl.contains(".jpg") -> "jpg"
            mediaUrl.contains(".jpeg") -> "jpeg"
            mediaUrl.contains(".png") -> "png"
            else -> "mp4" // Default to mp4 for videos
        }

        val fileName = "instagram_${System.currentTimeMillis()}.$extension"

        val request = DownloadManager.Request(Uri.parse(mediaUrl))
            .setTitle(fileName)
            .setDescription("Downloaded from Instagram")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .addRequestHeader("User-Agent", "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
            .addRequestHeader("Referer", "https://www.instagram.com/")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Monitor download
        monitorDownload(context, downloadId, fileName)

        Toast.makeText(context, "Download started: $fileName", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Log.e("Download", "Error starting download", e)
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
