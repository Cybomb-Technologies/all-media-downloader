package com.cybomb.allmediadownloader.fetcher

import com.cybomb.allmediadownloader.viewmodels.getCookiesForUrl
import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import android.net.Uri
import android.util.Log

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class InstagramFetcher {

    private fun normalizeInstagramUrl(postUrl: String): String {
        val uri = Uri.parse(postUrl)
        val pathSegments = uri.pathSegments

        if (pathSegments.size >= 2) {
            val type = pathSegments[0] // e.g., "p", "reel", "stories"
            val shortcode = pathSegments[1] // The unique code part

            if (type == "p" || type == "reel" || type == "tv") {
                return "https://www.instagram.com/$type/$shortcode/"
            }
        }
        return postUrl.split('?')[0].trimEnd('/') + "/"
    }

    suspend fun fetchMediaDetails(postUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        val normalizedUrl = normalizeInstagramUrl(postUrl)
       // Log.d("InstagramFetcher", "Normalized URL: $normalizedUrl")

        try {
            val userAgents = listOf(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1",
                "Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.162 Mobile Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
            )

            for (userAgent in userAgents) {
                val mediaInfo = tryExtractWithUserAgent(normalizedUrl, userAgent, cookies)
                if (mediaInfo != null) return@withContext mediaInfo
            }

            val embedUrl = normalizedUrl.replace("/reel/", "/p/").trimEnd('/') + "/embed/"
            if (!embedUrl.contains("/embed//embed/")) {
                val embedInfo = tryExtractWithUserAgent(embedUrl, userAgents.first(), cookies)
                if (embedInfo != null) return@withContext embedInfo
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e("InstagramFetcher", "Error fetching media", e)
            null
        }
    }


    private fun tryExtractWithUserAgent(url: String, userAgent: String, cookies: String?): DownloadMediaInfo? {
        return try {
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val requestBuilder = Request.Builder()
                .url(url)
                .header("User-Agent", userAgent)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Connection", "keep-alive")

            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null

            val html = response.body?.string() ?: return null
            val mediaUrl = extractMediaFromHtml(html)

            if (mediaUrl != null && isValidMediaUrl(mediaUrl)) {
                val type = if (mediaUrl.contains(".mp4")) "Video" else "Image"
                // *** FIX: Added fileName and postUrl ***
                return DownloadMediaInfo(
                    mediaUrl = mediaUrl,
                    type = type,
                    postUrl = url,
                    fileName = generateFileName(type)
                )
            }
            null
        } catch (e: Exception) {
            Log.e("InstagramFetcher", "Error with user agent", e)
            null
        }
    }

    // ... (rest of InstagramFetcher remains the same)
    private fun extractMediaFromHtml(html: String): String? {
        try {
            val sharedDataPattern = Pattern.compile("window\\._sharedData = (.*?);")
            val sharedDataMatch = sharedDataPattern.matcher(html)

            if (sharedDataMatch.find()) {
                val jsonString = sharedDataMatch.group(1)
                val jsonElement = JsonParser.parseString(jsonString)
                val jsonObject = jsonElement.asJsonObject

                val mediaNode = jsonObject
                    .getAsJsonObject("entry_data")
                    .getAsJsonArray("PostPage")?.get(0)
                    ?.getAsJsonObject()?.getAsJsonObject("graphql")
                    ?.getAsJsonObject("shortcode_media")

                if (mediaNode != null) {
                    if (mediaNode.has("video_url") && !mediaNode.get("video_url").isJsonNull) {
                        return mediaNode.get("video_url").asString
                    }

                    if (mediaNode.has("display_url") && !mediaNode.get("display_url").isJsonNull) {
                        return mediaNode.get("display_url").asString
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("InstagramFetcher", "Error parsing _sharedData JSON: ${e.message}")
        }


        val videoPattern1 = Pattern.compile("\"video_url\":\"([^\"]+)\"")
        val matcher1 = videoPattern1.matcher(html)
        if (matcher1.find()) {
            val videoUrl = matcher1.group(1)?.replace("\\u0026", "&")?.replace("\\/", "/")
            if (videoUrl?.contains(".mp4") == true) return videoUrl
        }

        val doc = Jsoup.parse(html)
        val videoUrl = doc.select("meta[property=og:video]").attr("content")
        if (videoUrl.isNotEmpty()) return videoUrl

        val imageUrl = doc.select("meta[property=og:image]").attr("content")
        if (imageUrl.isNotEmpty()) return imageUrl

        return null
    }

    private fun isValidMediaUrl(url: String): Boolean {
        return url.contains(".mp4") || url.contains(".jpg") ||
                url.contains(".jpeg") || url.contains(".png") ||
                url.contains("scontent") || url.contains("cdninstagram")
    }

}
