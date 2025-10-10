package com.cybomb.allmediadownloader.fetcher

import android.util.Log
import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class TwitterFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(22, TimeUnit.SECONDS)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun fetchMediaDetails(tweetUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder()
                .url(tweetUrl)
                .header("User-Agent", userAgent)

            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val htmlContent = response.body?.string() ?: return@withContext null

            // Extract from meta tags
            val doc = Jsoup.parse(htmlContent)

            // Try to get video first
            val videoMeta = doc.select("meta[property=og:video]")
            if (videoMeta.isNotEmpty()) {
                val videoUrl = videoMeta.attr("content")
                if (videoUrl.isNotEmpty() && videoUrl.contains(".mp4")) {
                    // *** FIX: Added fileName and explicitly passed postUrl ***
                    return@withContext DownloadMediaInfo(
                        postUrl = tweetUrl,
                        mediaUrl = videoUrl,
                        type = "Video",
                        fileName = generateFileName("Video")
                    )
                }
            }

            // Try to get image
            val imageMeta = doc.select("meta[property=og:image]")
            if (imageMeta.isNotEmpty()) {
                val imageUrl = imageMeta.attr("content")
                if (imageUrl.isNotEmpty()) {
                    // *** FIX: Added fileName and explicitly passed postUrl ***
                    return@withContext DownloadMediaInfo(
                        postUrl = tweetUrl,
                        mediaUrl = imageUrl,
                        type = "Image",
                        fileName = generateFileName("Image")
                    )
                }
            }

            // Alternative extraction for Twitter
            val scriptPattern = Pattern.compile("window\\.__INITIAL_STATE__ = (.*?);</script>", Pattern.DOTALL)
            val scriptMatcher = scriptPattern.matcher(htmlContent)

            if (scriptMatcher.find()) {
                val jsonString = scriptMatcher.group(1)
                try {
                    val jsonElement = JsonParser.parseString(jsonString)
                    // Twitter JSON structure is complex, this is a simplified approach
                    // In production, you'd need to navigate the actual JSON structure
                } catch (e: Exception) {
                    Log.e("TwitterFetcher", "Error parsing Twitter JSON", e)
                }
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e("TwitterFetcher", "Error fetching Twitter URL", e)
            return@withContext null
        }
    }
}
