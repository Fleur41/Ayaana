package com.sam.ayaana.Utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object MediaUtils {

    /**
     * Convert Uri to File for uploading
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // Create a temporary file
                val file = createTempFile(context, getMimeType(contentResolver, uri))
                FileOutputStream(file).use { outputStream ->
                    stream.copyTo(outputStream)
                }
                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get MIME type from Uri
     */
    private fun getMimeType(contentResolver: ContentResolver, uri: Uri): String {
        return when (uri.scheme) {
            ContentResolver.SCHEME_CONTENT -> contentResolver.getType(uri) ?: "image/jpeg"
            else -> {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension) ?: "image/jpeg"
            }
        }
    }

    /**
     * Create temporary file with proper extension
     */
    private fun createTempFile(context: Context, mimeType: String): File {
        val extension = when {
            mimeType.startsWith("image/") -> ".jpg"
            mimeType.startsWith("video/") -> ".mp4"
            else -> ".tmp"
        }
        return File.createTempFile("upload_", extension, context.cacheDir)
    }

    /**
     * Check if URI is for an image
     */
    fun isImageUri(contentResolver: ContentResolver, uri: Uri): Boolean {
        val mimeType = getMimeType(contentResolver, uri)
        return mimeType.startsWith("image/")
    }

    /**
     * Check if URI is for a video
     */
    fun isVideoUri(contentResolver: ContentResolver, uri: Uri): Boolean {
        val mimeType = getMimeType(contentResolver, uri)
        return mimeType.startsWith("video/")
    }

    fun getMediaType(context: Context, uri: Uri): MediaType {
        return when {
            isImageUri(context.contentResolver, uri) -> MediaType.IMAGE
            isVideoUri(context.contentResolver, uri) -> MediaType.VIDEO
            else -> MediaType.UNKNOWN
        }
    }
}

enum class MediaType {
    IMAGE, VIDEO, UNKNOWN
}
