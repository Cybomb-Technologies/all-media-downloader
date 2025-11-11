package com.cybomb.allmediadownloader.screens.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cybomb.allmediadownloader.viewmodels.generateFileName
import com.cybomb.allmediadownloader.viewmodels.DownloadMediaInfo
import com.cybomb.allmediadownloader.viewmodels.getCookiesForUrl
import com.cybomb.allmediadownloader.viewmodels.DownloaderViewModelFactory
import com.cybomb.allmediadownloader.viewmodels.deleteFile
import com.cybomb.allmediadownloader.viewmodels.formatFileSize
import com.cybomb.allmediadownloader.viewmodels.getFileNameFromUrl
import com.cybomb.allmediadownloader.viewmodels.getPlatformInfo
import com.cybomb.allmediadownloader.viewmodels.getSourcePreview
import com.cybomb.allmediadownloader.viewmodels.openFile
import com.cybomb.allmediadownloader.viewmodels.shareFile

@Composable
fun FileOptionsDialog(
    info: DownloadMediaInfo,
    context: Context,
    onDismiss: () -> Unit,
    onFileDeleted: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Options") },
        text = {
            Column {
                Text("Choose an action for this file:")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    getFileNameFromUrl(info.mediaUrl) ?: "Downloaded file",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Column {
                // Open button
                Button(
                    onClick = {
                        openFile(context, info)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Open File")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Share button
                OutlinedButton(
                    onClick = {
                        shareFile(context, info)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Delete button
                OutlinedButton(
                    onClick = {
                        deleteFile(context, info, onFileDeleted)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}