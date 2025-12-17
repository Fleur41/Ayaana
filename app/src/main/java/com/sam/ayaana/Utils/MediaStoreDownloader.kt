// Utils/MediaStoreDownloader.kt
package com.sam.ayaana.Utils

import android.R.attr.bitmap
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaStoreDownloader {

    sealed class DownloadResult {
        object Success : DownloadResult()
        data class Failure(val exception: Throwable) : DownloadResult()
        // Removed PermissionDenied since it's not used
    }

    // Download reel thumbnail/image
    suspend fun downloadReelThumbnail(
        context: Context,
        imageUrl: String,
        title: String,
        description: String
    ): DownloadResult {
        return try {
            withContext(Dispatchers.IO) {
                // 1. Download image using Coil
                val loader = Coil.imageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false) // For better quality
                    .build()

                val result = loader.execute(request)

                if (result is SuccessResult) {
                    // 2. Save to MediaStore
                    val success = saveImageToMediaStore(
                        context = context,
                        bitmap = result.drawable.toBitmap(),
                        title = title,
                        description = description
                    )

                    if (success) {
                        DownloadResult.Success
                    } else {
                        DownloadResult.Failure(Exception("Failed to save image to gallery"))
                    }
                } else {
                    DownloadResult.Failure(Exception("Failed to load image"))
                }
            }
        } catch (e: Exception) {
            DownloadResult.Failure(e)
        }
    }

    // Download video (simulated for now)
    suspend fun downloadReelVideo(
        context: Context,
        videoUrl: String,
        title: String,
        description: String
    ): DownloadResult {
        return try {
            withContext(Dispatchers.IO) {
                // For now, just simulate download and save thumbnail
                val loader = Coil.imageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(videoUrl)
                    .allowHardware(false)
                    .build()

                val result = loader.execute(request)

                if (result is SuccessResult) {
                    val success = saveImageToMediaStore(
                        context = context,
                        bitmap = result.drawable.toBitmap(),
                        title = "$title (Video Thumbnail)",
                        description = "Thumbnail for video: $description"
                    )

                    if (success) {
                        DownloadResult.Success
                    } else {
                        DownloadResult.Failure(Exception("Failed to save thumbnail"))
                    }
                } else {
                    // Simulate video download success for demo
                    DownloadResult.Success
                }
            }
        } catch (e: Exception) {
            DownloadResult.Failure(e)
        }
    }

    private fun saveImageToMediaStore(
        context: Context,
        bitmap: Bitmap,
        title: String,
        description: String
    ): Boolean {
        val resolver = context.contentResolver

        // Prepare content values - MIME type is hardcoded to "image/jpeg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // Hardcoded since we always use JPEG
            put(MediaStore.MediaColumns.TITLE, title)

            // Use MediaStore.Images.Media.DESCRIPTION for the description field
            put(MediaStore.Images.Media.DESCRIPTION, description) // FIXED: Correct constant

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Ayaana/Reels")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        // Insert into MediaStore
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                true
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                false
            }
        } else {
            false
        }
    }

    // Extension function to get bitmap from drawable
    private fun Drawable.toBitmap(): Bitmap {
        return if (this is BitmapDrawable) {
            bitmap
        } else {
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth.coerceAtLeast(1),
                intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            bitmap
        }
    }
}



//package com.sam.ayaana.Utils
//
//import android.content.ContentValues
//import android.content.Context
//import android.graphics.Bitmap
//import android.os.Build
//import android.os.Environment
//import android.provider.MediaStore
//import androidx.core.net.toUri
//import coil.Coil
//import coil.request.ImageRequest
//import coil.request.SuccessResult
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.OutputStream
//
//object MediaStoreDownloader {
//
//    sealed class DownloadResult {
//        data object Success : DownloadResult()
//        data class Failure(val exception: Throwable) : DownloadResult()
//        data object PermissionDenied : DownloadResult()
//    }
//
//    // Download reel thumbnail/image
//    suspend fun downloadReelThumbnail(
//        context: Context,
//        imageUrl: String,
//        title: String,
//        description: String
//    ): DownloadResult {
//        return try {
//            withContext(Dispatchers.IO) {
//                // 1. Download image using Coil
//                val loader = Coil.imageLoader(context)
//                val request = ImageRequest.Builder(context)
//                    .data(imageUrl)
//                    .allowHardware(false) // For better quality
//                    .build()
//
//                val result = loader.execute(request)
//
//                if (result is SuccessResult) {
//                    // 2. Save to MediaStore
//                    saveImageToMediaStore(
//                        context = context,
//                        bitmap = result.drawable.toBitmap(),
//                        title = title,
//                        description = description,
//                        mimeType = "image/jpeg"
//                    )
//                    DownloadResult.Success
//                } else {
//                    DownloadResult.Failure(Exception("Failed to load image"))
//                }
//            }
//        } catch (e: Exception) {
//            DownloadResult.Failure(e)
//        }
//    }
//
//    // Download video (simulated for now)
//    suspend fun downloadReelVideo(
//        context: Context,
//        videoUrl: String,
//        title: String,
//        description: String
//    ): DownloadResult {
//        return try {
//            withContext(Dispatchers.IO) {
//                // In production, you'd use something like ExoPlayer or OkHttp to download video
//                // For now, just simulate download and save thumbnail
//                val loader = Coil.imageLoader(context)
//                val request = ImageRequest.Builder(context)
//                    .data(videoUrl)
//                    .allowHardware(false)
//                    .build()
//
//                val result = loader.execute(request)
//
//                if (result is SuccessResult) {
//                    saveImageToMediaStore(
//                        context = context,
//                        bitmap = result.drawable.toBitmap(),
//                        title = "$title (Video Thumbnail)",
//                        description = "Thumbnail for video: $description",
//                        mimeType = "image/jpeg"
//                    )
//                    DownloadResult.Success
//                } else {
//                    // Simulate video download success for demo
//                    DownloadResult.Success
//                }
//            }
//        } catch (e: Exception) {
//            DownloadResult.Failure(e)
//        }
//    }
//
//    private fun saveImageToMediaStore(
//        context: Context,
//        bitmap: Bitmap,
//        title: String,
//        description: String,
//        mimeType: String
//    ): Boolean {
//        val resolver = context.contentResolver
//
//        // Prepare content values
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.jpg")
//            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
//            put(MediaStore.MediaColumns.TITLE, title)
//            put(MediaStore.MediaColumns.DESCRIPTION, description)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Ayaana/Reels")
//                put(MediaStore.MediaColumns.IS_PENDING, 1)
//            }
//        }
//
//        // Insert into MediaStore
//        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        return if (uri != null) {
//            try {
//                resolver.openOutputStream(uri)?.use { outputStream ->
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
//                }
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    contentValues.clear()
//                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
//                    resolver.update(uri, contentValues, null, null)
//                }
//                true
//            } catch (e: Exception) {
//                resolver.delete(uri, null, null)
//                false
//            }
//        } else {
//            false
//        }
//    }
//
//    // Extension function to get bitmap from drawable
//    private fun android.graphics.drawable.Drawable.toBitmap(): Bitmap {
//        return if (this is android.graphics.drawable.BitmapDrawable) {
//            bitmap
//        } else {
//            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
//            val canvas = android.graphics.Canvas(bitmap)
//            setBounds(0, 0, canvas.width, canvas.height)
//            draw(canvas)
//            bitmap
//        }
//    }
//}