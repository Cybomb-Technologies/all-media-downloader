package com.cybomb.allmediadownloader.screens.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo


@Composable
fun DownloadContentList(
    contentType: String,
    downloadedFiles: SnapshotStateList<DownloadMediaInfo>,
    context: Context
) {
    // Filter the global list based on the selected tab
    val itemsToShow = remember(contentType, downloadedFiles.size) {
        downloadedFiles.filter {
            when (contentType) {
                "Image" -> it.type.contains("Image", ignoreCase = true) || it.type.contains("Thumbnail", ignoreCase = true)
                "Video" -> it.type.contains("Video", ignoreCase = true)
                else -> true // All Files
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (itemsToShow.isNotEmpty()) "Downloaded $contentType (${itemsToShow.size})" else "No $contentType found",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (itemsToShow.isNotEmpty()) {
                    TextButton(onClick = {
                        // Clear all downloads
                        downloadedFiles.clear()
                        Toast.makeText(context, "All downloads cleared", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Clear All")
                    }
                }
            }
        }

        items(itemsToShow.size) { index ->
            FileListItem(info = itemsToShow[index], context = context)
        }

        item {
            if (itemsToShow.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = "Empty",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No $contentType found yet",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Start downloading from your favorite platforms!",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
