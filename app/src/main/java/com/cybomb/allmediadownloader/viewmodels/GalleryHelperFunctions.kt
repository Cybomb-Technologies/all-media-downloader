package com.cybomb.allmediadownloader.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import java.io.File

fun openFile(context: Context, info: DownloadMediaInfo) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val file = File(info.mediaUrl)

        // For downloaded files, you'd need to get the actual file path from your storage
        val uri = if (info.mediaUrl.startsWith("http")) {
            // If it's still a URL (not yet downloaded properly), show a message
            Toast.makeText(context, "File location not available", Toast.LENGTH_SHORT).show()
            return
        } else {
            // THIS IS THE CORRECT LINE: Use FileProvider to get a secure URI
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val mimeType = when {
            info.type.contains("Video", ignoreCase = true) -> "video/*"
            info.type.contains("Image", ignoreCase = true) -> "image/*"
            else -> "*/*"
        }

        intent.setDataAndType(uri, mimeType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Open with"))
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun shareFile(context: Context, info: DownloadMediaInfo) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        val file = File(info.mediaUrl)

        // For downloaded files, you'd need to get the actual file path
        val uri = if (info.mediaUrl.startsWith("http")) {
            // If it's still a URL, share the URL instead
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, info.mediaUrl)
            context.startActivity(Intent.createChooser(intent, "Share via"))
            return
        } else {
            // THIS IS THE CORRECT LINE: Use FileProvider to get a secure URI
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

        val mimeType = when {
            info.type.contains("Video", ignoreCase = true) -> "video/*"
            info.type.contains("Image", ignoreCase = true) -> "image/*"
            else -> "*/*"
        }

        intent.type = mimeType
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Share via"))
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot share file: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun deleteFile(context: Context, info: DownloadMediaInfo, onFileDeleted: () -> Unit) {
    // TODO: Implement actual file deletion logic
    // This would involve:
    // 1. Finding the actual file in storage
    // 2. Deleting the file
    // 3. Updating the database/list
    // 4. Calling onFileDeleted callback

    Toast.makeText(context, "Delete functionality to be implemented", Toast.LENGTH_SHORT).show()
}

fun getFileNameFromUrl(url: String): String? {
    return try {
        Uri.parse(url).lastPathSegment
    } catch (e: Exception) {
        null
    }
}

fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}

fun getSourcePreview(url: String): String {
    return try {
        val uri = Uri.parse(url)
        "${uri.host ?: "unknown"}${uri.path?.take(30) ?: ""}..."
    } catch (e: Exception) {
        "Unknown source"
    }
}

// Update the DownloadMediaInfo data class to include more file information
data class DownloadMediaInfo(
    val postUrl: String,
    val mediaUrl: String,
    val type: String, // "Video" or "Image"
    val size: Long? = null,
    val fileName: String? = null,
    val downloadDate: Long = System.currentTimeMillis(),
    val platform: String? = null
)

// Helper functions
fun getPlatformInfo(url: String): Pair<String, Color> {
    return when {
        url.contains("instagram.com", ignoreCase = true) -> "Instagram" to Color(0xFFC13584)
        url.contains("youtube.com", ignoreCase = true) || url.contains("youtu.be", ignoreCase = true) -> "YouTube" to Color(0xFFFF0000)
        url.contains("pinterest.com", ignoreCase = true) || url.contains("pin.it", ignoreCase = true) -> "Pinterest" to Color(0xFFE60023)
        url.contains("facebook.com", ignoreCase = true) -> "Facebook" to Color(0xFF1877F2)
        url.contains("twitter.com", ignoreCase = true) || url.contains("x.com", ignoreCase = true) -> "Twitter/X" to Color(0xFF1DA1F2)
        url.contains("reddit.com", ignoreCase = true) -> "Reddit" to Color(0xFFFF5700)
        url.contains("tiktok.com", ignoreCase = true) -> "TikTok" to Color(0xFF000000)
        url.contains("snapchat.com", ignoreCase = true) -> "Snapchat" to Color(0xFFFFFC00)
        url.contains("linkedin.com", ignoreCase = true) -> "LinkedIn" to Color(0xFF0077B5)
        url.contains("dailymotion.com", ignoreCase = true) -> "Dailymotion" to Color(0xFF0066DC)
        url.contains("vimeo.com", ignoreCase = true) -> "Vimeo" to Color(0xFF1AB7EA)
        else -> "Web" to Color(0xFF666666)
    }
}
