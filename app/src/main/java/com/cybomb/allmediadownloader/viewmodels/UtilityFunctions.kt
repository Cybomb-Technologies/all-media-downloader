package com.cybomb.allmediadownloader.viewmodels

import android.webkit.CookieManager


/**
 * Generates a unique, time-stamped file name based on the content type.
 */
fun generateFileName(type: String): String {
    val extension = when {
        type.contains("Video", ignoreCase = true) -> "mp4"
        type.contains("Image", ignoreCase = true) || type.contains("Thumbnail", ignoreCase = true) -> "jpg"
        else -> "dat"
    }
    return "download_${System.currentTimeMillis()}.$extension"
}

/**
 * Retrieves cookies for a given URL from the system CookieManager.
 */
fun getCookiesForUrl(url: String): String? {
    // CookieManager handles persistence across the WebView and the app's http clients
    return CookieManager.getInstance().getCookie(url)
}
