package com.cybomb.allmediadownloader.fetcher

import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class PinterestFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(22, TimeUnit.SECONDS)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun fetchMediaDetails(pinUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder()
                .url(pinUrl)
                .header("User-Agent", userAgent)

            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val htmlContent = response.body?.string() ?: return@withContext null

            // Extract from JSON-LD
            val jsonLdPattern = Pattern.compile("<script type=\"application/ld\\+json\">(.*?)</script>", Pattern.DOTALL)
            val jsonLdMatcher = jsonLdPattern.matcher(htmlContent)

            if (jsonLdMatcher.find()) {
                val jsonString = jsonLdMatcher.group(1)
                try {
                    val jsonElement = JsonParser.parseString(jsonString)
                    val jsonObject = jsonElement.asJsonObject

                    // Try to get contentUrl for video or image
                    if (jsonObject.has("contentUrl")) {
                        val mediaUrl = jsonObject.get("contentUrl").asString
                        val type = if (mediaUrl.contains(".mp4")) "Video" else "Image"
                        // *** FIX: Added fileName and explicitly passed postUrl ***
                        return@withContext DownloadMediaInfo(
                            postUrl = pinUrl,
                            mediaUrl = mediaUrl,
                            type = type,
                            fileName = generateFileName(type)
                        )
                    }

                    // Try for image
                    if (jsonObject.has("image")) {
                        val imageUrl = jsonObject.getAsJsonObject("image").get("contentUrl").asString
                        // *** FIX: Added fileName and explicitly passed postUrl ***
                        return@withContext DownloadMediaInfo(
                            postUrl = pinUrl,
                            mediaUrl = imageUrl,
                            type = "Image",
                            fileName = generateFileName("Image")
                        )
                    }
                } catch (e: Exception) {
                    Log.e("PinterestFetcher", "Error parsing JSON-LD", e)
                }
            }

            // Fallback to meta tags
            val doc = Jsoup.parse(htmlContent)
            val videoUrl = doc.select("meta[property=og:video]").attr("content")
            if (videoUrl.isNotEmpty()) {
                // *** FIX: Added fileName and explicitly passed postUrl ***
                return@withContext DownloadMediaInfo(
                    postUrl = pinUrl,
                    mediaUrl = videoUrl,
                    type = "Video",
                    fileName = generateFileName("Video")
                )
            }

            val imageUrl = doc.select("meta[property=og:image]").attr("content")
            if (imageUrl.isNotEmpty()) {
                // *** FIX: Added fileName and explicitly passed postUrl ***
                return@withContext DownloadMediaInfo(
                    postUrl = pinUrl,
                    mediaUrl = imageUrl,
                    type = "Image",
                    fileName = generateFileName("Image")
                )
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e("PinterestFetcher", "Error fetching Pinterest URL", e)
            return@withContext null
        }
    }
}
