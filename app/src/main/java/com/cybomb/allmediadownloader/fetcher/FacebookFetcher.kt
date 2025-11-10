package com.cybomb.allmediadownloader.fetcher

import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class FacebookFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(22, TimeUnit.SECONDS)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun fetchMediaDetails(videoUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder()
                .url(videoUrl)
                .header("User-Agent", userAgent)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")

            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val htmlContent = response.body?.string() ?: return@withContext null

            // --- VIDEO EXTRACTION (Keep existing logic) ---

            // Try to extract HD quality video first
            val hdVideoPattern = Pattern.compile("\"hd_src\":\"(.*?)\"")
            val hdMatcher = hdVideoPattern.matcher(htmlContent)
            if (hdMatcher.find()) {
                val mediaUrl = hdMatcher.group(1).replace("\\u0025", "%").replace("\\/", "/")
                if (mediaUrl.isNotEmpty()) {
                    return@withContext DownloadMediaInfo(
                        postUrl = videoUrl,
                        mediaUrl = mediaUrl,
                        type = "Video",
                        fileName = generateFileName("Video")
                    )
                }
            }

            // Fallback to SD quality
            val sdVideoPattern = Pattern.compile("\"sd_src\":\"(.*?)\"")
            val sdMatcher = sdVideoPattern.matcher(htmlContent)
            if (sdMatcher.find()) {
                val mediaUrl = sdMatcher.group(1).replace("\\u0025", "%").replace("\\/", "/")
                if (mediaUrl.isNotEmpty()) {
                    return@withContext DownloadMediaInfo(
                        postUrl = videoUrl,
                        mediaUrl = mediaUrl,
                        type = "Video",
                        fileName = generateFileName("Video")
                    )
                }
            }

            // --- IMAGE EXTRACTION (NEW LOGIC) ---

            val doc = Jsoup.parse(htmlContent)

            // 1. Check for og:video (Fallback for video if JSON fails)
            val ogVideo = doc.select("meta[property=og:video]").attr("content")
            if (ogVideo.isNotEmpty()) {
                return@withContext DownloadMediaInfo(
                    postUrl = videoUrl,
                    mediaUrl = ogVideo,
                    type = "Video",
                    fileName = generateFileName("Video")
                )
            }

            // 2. Check for og:image (Extraction for image)
            val ogImage = doc.select("meta[property=og:image]").attr("content")
            if (ogImage.isNotEmpty()) {
                // Ensure it's a valid image URL before returning
                if (isValidImageUrl(ogImage)) {
                    return@withContext DownloadMediaInfo(
                        postUrl = videoUrl,
                        mediaUrl = ogImage,
                        type = "Image", // Explicitly set type to Image
                        fileName = generateFileName("Image")
                    )
                }
            }

            // Additional image extraction can be added here if og:image fails
            // (e.g., searching for known image CDN URLs in the HTML)

            return@withContext null
        } catch (e: Exception) {
            Log.e("FacebookFetcher", "Error fetching Facebook URL", e)
            return@withContext null
        }
    }

    // You'll need to define this helper function outside the suspend block,
    // maybe as a private function in FacebookFetcher or a utility function.
    private fun isValidImageUrl(url: String): Boolean {
        return url.contains(".jpg", ignoreCase = true) ||
                url.contains(".jpeg", ignoreCase = true) ||
                url.contains(".png", ignoreCase = true) ||
                url.contains(".gif", ignoreCase = true)
    }
}
//class FacebookFetcher {
//    private val client = OkHttpClient.Builder()
//        .connectTimeout(20, TimeUnit.SECONDS)
//        .readTimeout(22, TimeUnit.SECONDS)
//        .build()
//
//    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
//
//    suspend fun fetchMediaDetails(videoUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
//        try {
//            val requestBuilder = Request.Builder()
//                .url(videoUrl)
//                .header("User-Agent", userAgent)
//                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
//
//            cookies?.let {
//                requestBuilder.header("Cookie", it)
//            }
//
//            val request = requestBuilder.build()
//            val response = client.newCall(request).execute()
//            if (!response.isSuccessful) return@withContext null
//
//            val htmlContent = response.body?.string() ?: return@withContext null
//
//            // Try to extract HD quality video first
//            val hdVideoPattern = Pattern.compile("\"hd_src\":\"(.*?)\"")
//            val hdMatcher = hdVideoPattern.matcher(htmlContent)
//            if (hdMatcher.find()) {
//                val mediaUrl = hdMatcher.group(1).replace("\\u0025", "%").replace("\\/", "/")
//                if (mediaUrl.isNotEmpty()) {
//                    // *** FIX: Added fileName and explicitly passed postUrl ***
//                    return@withContext DownloadMediaInfo(
//                        postUrl = videoUrl,
//                        mediaUrl = mediaUrl,
//                        type = "Video",
//                        fileName = generateFileName("Video")
//                    )
//                }
//            }
//
//            // Fallback to SD quality
//            val sdVideoPattern = Pattern.compile("\"sd_src\":\"(.*?)\"")
//            val sdMatcher = sdVideoPattern.matcher(htmlContent)
//            if (sdMatcher.find()) {
//                val mediaUrl = sdMatcher.group(1).replace("\\u0025", "%").replace("\\/", "/")
//                if (mediaUrl.isNotEmpty()) {
//                    // *** FIX: Added fileName and explicitly passed postUrl ***
//                    return@withContext DownloadMediaInfo(
//                        postUrl = videoUrl,
//                        mediaUrl = mediaUrl,
//                        type = "Video",
//                        fileName = generateFileName("Video")
//                    )
//                }
//            }
//
//            // Try OpenGraph as last resort
//            val doc = Jsoup.parse(htmlContent)
//            val ogVideo = doc.select("meta[property=og:video]").attr("content")
//            if (ogVideo.isNotEmpty()) {
//                // *** FIX: Added fileName and explicitly passed postUrl ***
//                return@withContext DownloadMediaInfo(
//                    postUrl = videoUrl,
//                    mediaUrl = ogVideo,
//                    type = "Video",
//                    fileName = generateFileName("Video")
//                )
//            }
//
//            return@withContext null
//        } catch (e: Exception) {
//            Log.e("FacebookFetcher", "Error fetching Facebook URL", e)
//            return@withContext null
//        }
//    }
//}
