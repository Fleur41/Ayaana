package com.sam.ayaana.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtils {

    /**
     * Copy image from gallery Uri to app's private storage
     * Returns the permanent file path
     */
    suspend fun copyImageToAppStorage(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Create unique filename
                val fileName = "profile_${UUID.randomUUID()}.jpg"

                // Get app's private files directory
                val storageDir = context.filesDir
                val destinationFile = File(storageDir, fileName)

                // Open input stream from gallery Uri
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    // Copy to app's storage
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                // Return the permanent file path
                destinationFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Get existing profile image from app storage
     */
    suspend fun getExistingProfileImagePath(context: Context): String? {
        return withContext(Dispatchers.IO) {
            val storageDir = context.filesDir
            Log.d("ImageUtils", "Checking storage dir: ${storageDir.absolutePath}")

            // List all files
            val allFiles = storageDir.listFiles()
            Log.d("ImageUtils", "All files in storage: ${allFiles?.map { it.name }}")

            // Look for profile image files
            val files = storageDir.listFiles { file ->
                file.name.startsWith("profile_") &&
                        file.extension.lowercase() in listOf("jpg", "jpeg", "png")
            }

            Log.d("ImageUtils", "Profile image files found: ${files?.map { it.absolutePath }}")

            // Return the most recent file (if multiple exist)
            files?.maxByOrNull { it.lastModified() }?.absolutePath
        }
    }


    /**
     * Clean up old profile images, keep only the current one
     */
    suspend fun cleanUpOldProfileImages(context: Context, keepCurrentPath: String? = null) {
        withContext(Dispatchers.IO) {
            val storageDir = context.filesDir
            val files = storageDir.listFiles { file ->
                file.name.startsWith("profile_") &&
                        file.extension.lowercase() in listOf("jpg", "jpeg", "png")
            }

            files?.forEach { file ->
                // Delete all except the current one
                if (file.absolutePath != keepCurrentPath) {
                    file.delete()
                }
            }
        }
    }
}