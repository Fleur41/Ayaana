package com.sam.ayaana.Utils

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

object GalleryPicker {

    const val MAX_SELECTION_POST = 10
    const val MAX_SELECTION_STORY = 1
    const val MAX_SELECTION_REEL = 1

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun rememberGalleryLauncher(
        onMediaSelected: (List<Uri>) -> Unit,
        maxSelection: Int = 1,
        isVideoOnly: Boolean = false
    ): GalleryLauncherResult {
        val permissionState = rememberPermissionState(
            permission = Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            result.data?.let { intent ->
                val uris = getSelectedUris(intent, maxSelection)
                onMediaSelected(uris)
            }
        }

        // Handle permission request automatically
        LaunchedEffect(permissionState) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }

        return GalleryLauncherResult(
            permissionState = permissionState, // FIXED: No casting needed
            launchGallery = {
                if (permissionState.status.isGranted) {
                    val intent = createGalleryIntent(maxSelection, isVideoOnly)
                    galleryLauncher.launch(intent)
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        )
    }

    private fun createGalleryIntent(maxSelection: Int, isVideoOnly: Boolean): Intent {
        return Intent(Intent.ACTION_PICK).apply {
            type = when {
                isVideoOnly -> "video/*"
                maxSelection > 1 -> "image/* video/*"
                else -> "*/*"
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, maxSelection > 1)
            putExtra(Intent.EXTRA_MIME_TYPES, getMimeTypes(isVideoOnly))
            action = Intent.ACTION_GET_CONTENT
        }
    }

    private fun getMimeTypes(isVideoOnly: Boolean): Array<String> {
        return when {
            isVideoOnly -> arrayOf("video/mp4", "video/3gpp", "video/x-matroska")
            else -> arrayOf("image/jpeg", "image/png", "image/gif", "video/mp4", "video/3gpp")
        }
    }

    private fun getSelectedUris(intent: Intent, maxSelection: Int): List<Uri> {
        return when {
            intent.clipData != null -> {
                // Multiple selection
                val uris = mutableListOf<Uri>()
                val clipData = intent.clipData!!
                for (i in 0 until minOf(clipData.itemCount, maxSelection)) {
                    clipData.getItemAt(i).uri?.let { uris.add(it) }
                }
                uris
            }
            intent.data != null -> {
                // Single selection
                listOf(intent.data!!)
            }
            else -> emptyList()
        }
    }
}

data class GalleryLauncherResult @OptIn(ExperimentalPermissionsApi::class) constructor(
    val permissionState: PermissionState, // FIXED: Use PermissionState directly
    val launchGallery: () -> Unit
)
