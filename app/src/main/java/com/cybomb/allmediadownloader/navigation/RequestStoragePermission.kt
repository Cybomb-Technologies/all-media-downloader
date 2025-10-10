package com.cybomb.allmediadownloader.navigation

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

@Composable
fun RequestStoragePermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
                if (!granted)
                    Toast.makeText(context, "Storage permission needed", Toast.LENGTH_SHORT).show()
            }
        )

        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
   // Log.d("Permission", "Storage permission simulated")

}



fun checkStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+ doesn't need WRITE_EXTERNAL_STORAGE for Downloads folder
        true
    } else {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}
//
//// Call this before starting download
//fun RequestStoragePermission(activity: Activity) {
//    if (!checkStoragePermission(activity)) {
//        ActivityCompat.requestPermissions(
//            activity,
//            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//            1001
//        )
//    }
//}

