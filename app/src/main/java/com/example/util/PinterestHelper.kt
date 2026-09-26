package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object PinterestHelper {
    const val PINTEREST_SEARCH_URL = "https://tr.pinterest.com/search/pins/?q=cuma%20mesajlar%C4%B1%20cami%20ve%20g%C3%BCzel&rs=typed"

    data class MosqueCardAsset(
        val key: String,
        val title: String,
        val localRes: Int? = null,
        val recommendedPrayer: String,
        val tag: String
    )

    val curatedMosqueGallery = listOf(
        MosqueCardAsset(
            key = "mosque_sunset",
            title = "Sultanahmet Günbatımı & Kuşlar",
            localRes = R.drawable.img_mosque_sunset,
            recommendedPrayer = "Günün batışıyla dertleriniz son bulsun, Cuma vaktinin feyziyle kalbiniz huzur dolsun. Rabbim tüm hayırlı dualarınızı kabul eylesin {isim}.",
            tag = "Günbatımı & Huzur"
        ),
        MosqueCardAsset(
            key = "mosque_spiritual",
            title = "Manevi Avlu, Güller & Kandiller",
            localRes = R.drawable.img_mosque_spiritual,
            recommendedPrayer = "Gül kokulu, nurlu bir Cuma gününe uyanmayı nasip eden Rabbimize hamd olsun. Hanenizden bereket, kalbinizden sevgi eksik olmasın.",
            tag = "Güller & Bereket"
        ),
        MosqueCardAsset(
            key = "mosque_night",
            title = "Mavi Camii ve Hilal Gecesi",
            localRes = R.drawable.img_mosque_night,
            recommendedPrayer = "Karanlıkları nuruyla aydınlatan Yüce Allah, bu mübarek Cuma hürmetine gönlünüzdeki tüm hüzünleri sevince çevirsin. Hayırlı Cumalar {isim}!",
            tag = "Hilal & Cami"
        ),
        MosqueCardAsset(
            key = "mosque_interior",
            title = "Ulu Cami Hat & Işık Huzmeleri",
            localRes = R.drawable.img_mosque_interior,
            recommendedPrayer = "Birlik ve huşû ile secdeye varan, dualarda buluşan kullardan eylesin. Cumanız mübarek, ameliniz makbul olsun.",
            tag = "Hat & Maneviyat"
        )
    )

    private val httpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    /**
     * Downloads an image from a URL or Pinterest image link and saves it to internal storage.
     * Returns local absolute path or null.
     */
    suspend fun downloadImageFromUrl(context: Context, imageUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val directUrl = resolvePinterestImageUrl(imageUrl) ?: imageUrl

            val request = Request.Builder()
                .url(directUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body ?: return@withContext null
            val bytes = responseBody.bytes()

            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null

            val imagesDir = File(context.filesDir, "custom_mosque_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            val fileName = "cuma_pinterest_${System.currentTimeMillis()}.jpg"
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

    /**
     * If the user pastes a Pinterest Pin web page link, extracts the high-resolution pin image URL.
     */
    suspend fun resolvePinterestImageUrl(pageUrl: String): String? = withContext(Dispatchers.IO) {
        if (pageUrl.endsWith(".jpg") || pageUrl.endsWith(".jpeg") || pageUrl.endsWith(".png") || pageUrl.endsWith(".webp")) {
            return@withContext pageUrl
        }

        try {
            val request = Request.Builder()
                .url(pageUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: return@withContext null

            val pattern = Pattern.compile("https://i\\.pinimg\\.com/[0-9a-zA-Z_/\\-]+\\.(?:jpg|png|jpeg)")
            val matcher = pattern.matcher(html)
            if (matcher.find()) {
                val match = matcher.group()
                return@withContext match.replace("/236x/", "/736x/").replace("/474x/", "/736x/")
            }
        } catch (_: Exception) {
        }
        null
    }

    fun generateMatchingPrayerForImage(imageKey: String, senderSignature: String = ""): String {
        val asset = curatedMosqueGallery.find { it.key == imageKey }
        val prayerBase = asset?.recommendedPrayer ?: "Allah'ım bu mübarek Cuma gününde gönüllerimizi nurlandır, hanelerimize huzur ve bereket nasip eyle."

        return if (senderSignature.isNotBlank()) {
            "$prayerBase\n\nSelam ve dua ile,\n$senderSignature"
        } else {
            prayerBase
        }
    }
}
