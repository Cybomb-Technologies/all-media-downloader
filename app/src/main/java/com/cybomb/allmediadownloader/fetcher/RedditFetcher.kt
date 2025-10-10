package com.cybomb.allmediadownloader.fetcher

import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.viewmodels.generateFileName

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RedditFetcher {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(22, TimeUnit.SECONDS)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"

    suspend fun fetchMediaDetails(redditUrl: String, cookies: String? = null): DownloadMediaInfo? = withContext(Dispatchers.IO) {
        try {
            // Add .json to the URL to get JSON data
            val jsonUrl = if (!redditUrl.endsWith(".json")) "$redditUrl.json" else redditUrl

            val requestBuilder = Request.Builder()
                .url(jsonUrl)
                .header("User-Agent", userAgent)

            cookies?.let {
                requestBuilder.header("Cookie", it)
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val jsonContent = response.body?.string() ?: return@withContext null

            try {
                val jsonArray = JsonParser.parseString(jsonContent).asJsonArray
                val postData = jsonArray[0].asJsonObject
                    .getAsJsonObject("data")
                    .getAsJsonArray("children")[0]
                    .asJsonObject
                    .getAsJsonObject("data")

                // Check for video
                if (postData.has("secure_media") && !postData.get("secure_media").isJsonNull) {
                    val secureMedia = postData.getAsJsonObject("secure_media")
                    if (secureMedia.has("reddit_video")) {
                        val videoData = secureMedia.getAsJsonObject("reddit_video")
                        val videoUrl = videoData.get("fallback_url").asString
                        // *** FIX: Added fileName and explicitly passed postUrl ***
                        return@withContext DownloadMediaInfo(
                            postUrl = redditUrl,
                            mediaUrl = videoUrl,
                            type = "Video",
                            fileName = generateFileName("Video")
                        )
                    }
                }

                // Check for gallery images
                if (postData.has("gallery_data")) {
                    val galleryData = postData.getAsJsonObject("gallery_data")
                    val mediaMetadata = postData.getAsJsonObject("media_metadata")

                    if (galleryData.has("items")) {
                        val items = galleryData.getAsJsonArray("items")
                        if (items.size() > 0) {
                            val firstItem = items[0].asJsonObject
                            val mediaId = firstItem.get("media_id").asString
                            if (mediaMetadata.has(mediaId)) {
                                val imageData = mediaMetadata.getAsJsonObject(mediaId)
                                if (imageData.has("s")) {
                                    val imageUrl = imageData.getAsJsonObject("s").get("u").asString
                                    // *** FIX: Added fileName and explicitly passed postUrl ***
                                    return@withContext DownloadMediaInfo(
                                        postUrl = redditUrl,
                                        mediaUrl = imageUrl,
                                        type = "Image",
                                        fileName = generateFileName("Image")
                                    )
                                }
                            }
                        }
                    }
                }

                // Check for single image
                if (postData.has("url_overridden_by_dest")) {
                    val imageUrl = postData.get("url_overridden_by_dest").asString
                    if (imageUrl.endsWith(".jpg") || imageUrl.endsWith(".png") || imageUrl.endsWith(".gif")) {
                        // *** FIX: Added fileName and explicitly passed postUrl ***
                        return@withContext DownloadMediaInfo(
                            postUrl = redditUrl,
                            mediaUrl = imageUrl,
                            type = "Image",
                            fileName = generateFileName("Image")
                        )
                    }
                }

                // Check for external links (Imgur, etc.)
                if (postData.has("preview")) {
                    val preview = postData.getAsJsonObject("preview")
                    val images = preview.getAsJsonArray("images")
                    if (images.size() > 0) {
                        val imageUrl = images[0].asJsonObject.get("source").asJsonObject.get("url").asString
                        // *** FIX: Added fileName and explicitly passed postUrl ***
                        return@withContext DownloadMediaInfo(
                            postUrl = redditUrl,
                            mediaUrl = imageUrl,
                            type = "Image",
                            fileName = generateFileName("Image")
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("RedditFetcher", "Error parsing Reddit JSON", e)
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e("RedditFetcher", "Error fetching Reddit URL", e)
            return@withContext null
        }
    }
}
