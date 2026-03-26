package com.cybomb.allmediadownloader.fetcher

import android.util.Log
import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class YouTubeFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(22, TimeUnit.SECONDS)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    /**
     * Attempts to extract a direct video URL from a YouTube watch or shorts URL.
     * ⭐️ MODIFIED: Now accepts cookies and adds them to the request header.
     */
    suspend fun fetchMediaDetails(videoUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        val url = if (videoUrl.contains("/shorts/")) {
            // Convert shorts URL to watch URL for better scraping results
            videoUrl.replace("/shorts/", "/watch?v=")
        } else {
            videoUrl
        }

        try {
            val requestBuilder = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)

            // ⭐️ ADDED: Pass cookies to the request
            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val htmlContent = response.body?.string() ?: return@withContext null

            // 1. Regex to find the 'hlsManifestUrl' or a direct stream URL from the embedded data
            val regex = Pattern.compile("\"hlsManifestUrl\":\"(.*?)\"")
            val matcher = regex.matcher(htmlContent)

            if (matcher.find()) {
                val directUrl = matcher.group(1).replace("\\u0026", "&")
                if (directUrl.isNotEmpty()) {
                    // *** FIX: Added fileName and removed size ***
                    return@withContext DownloadMediaInfo(
                        postUrl = videoUrl,
                        mediaUrl = directUrl,
                        type = "Video",
                        fileName = generateFileName("Video")
                    )
                }
            }

            // 2. Fallback: OpenGraph fallback (usually just the thumbnail)
            val doc = Jsoup.parse(htmlContent)
            val ogImageUrl = doc.select("meta[property=og:image]").attr("content")

            if (ogImageUrl.isNotEmpty()) {
                // If only the thumbnail is found, return it as "Image"
                // *** FIX: Added fileName and removed size ***
                return@withContext DownloadMediaInfo(
                    postUrl = videoUrl,
                    mediaUrl = ogImageUrl,
                    type = "Thumbnail (Image)",
                    fileName = generateFileName("Image")
                )
            }


            return@withContext null
        } catch (e: Exception) {
            Log.e("YouTubeFetcher", "Error fetching YouTube URL", e)
            return@withContext null
        }
    }
}
