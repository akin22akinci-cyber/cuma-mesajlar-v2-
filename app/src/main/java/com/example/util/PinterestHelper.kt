package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object PinterestHelper {
    const val PINTEREST_SEARCH_URL = "https://tr.pinterest.com/search/pins/?q=cuma%20mesajlar%C4%B1%20cami%20ve%20g%C3%BCzel&rs=typed"

    data class GalleryPhoto(
        val key: String,
        val title: String,
        val localRes: Int? = null,
        val remoteUrl: String? = null,
        val description: String
    )

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    /**
     * Downloads an image from a URL and saves it to internal storage.
     * Returns the local file path or null if failed.
     */
    suspend fun downloadImageFromUrl(context: Context, imageUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(imageUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body ?: return@withContext null
            val bytes = responseBody.bytes()

            // Verify it's a valid bitmap
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null

            val imagesDir = File(context.filesDir, "custom_mosque_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            val fileName = "cuma_photo_${System.currentTimeMillis()}.jpg"
            val file = File(imagesDir, fileName)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
