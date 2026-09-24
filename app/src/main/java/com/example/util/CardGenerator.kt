package com.example.util

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object CardGenerator {

    suspend fun generateCardImage(
        context: Context,
        imageKeyOrPath: String,
        messageText: String,
        signature: String = "",
        recipientName: String = ""
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // Load base bitmap
            val baseBitmap: Bitmap = loadBitmap(context, imageKeyOrPath)

            // Scale to standard card dimension (e.g. 1080 x 1440)
            val targetWidth = 1080
            val targetHeight = 1440
            val cardBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(cardBitmap)

            // Draw and scale base photo nicely to fill
            val srcRect = Rect(0, 0, baseBitmap.width, baseBitmap.height)
            val dstRect = Rect(0, 0, targetWidth, targetHeight)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(baseBitmap, srcRect, dstRect, paint)

            // Draw dark vignette & gradient over the lower 70% of image for great text readability
            val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, targetHeight * 0.25f,
                    0f, targetHeight.toFloat(),
                    intArrayOf(
                        android.graphics.Color.argb(40, 0, 0, 0),
                        android.graphics.Color.argb(190, 6, 28, 20),
                        android.graphics.Color.argb(240, 4, 18, 13)
                    ),
                    floatArrayOf(0f, 0.45f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), gradientPaint)

            // Draw top subtle vignette
            val topVignette = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, 0f, 300f,
                    intArrayOf(
                        android.graphics.Color.argb(160, 0, 20, 14),
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), 300f, topVignette)

            // Gold ornate frame
            val goldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#E5BE65")
                style = Paint.Style.STROKE
                strokeWidth = 4f
            }
            val frameMargin = 48f
            canvas.drawRect(
                frameMargin, frameMargin,
                targetWidth - frameMargin, targetHeight - frameMargin,
                goldPaint
            )

            // Inner thin line
            val innerGoldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#99E5BE65")
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }
            canvas.drawRect(
                frameMargin + 12f, frameMargin + 12f,
                targetWidth - frameMargin - 12f, targetHeight - frameMargin - 12f,
                innerGoldPaint
            )

            // Header "HAYIRLI CUMALAR"
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#FFF3D0")
                textSize = 58f
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                setShadowLayer(8f, 0f, 4f, android.graphics.Color.BLACK)
            }

            // Subtitle / Bismillah ornament
            val ornamentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#E5BE65")
                textSize = 34f
                textAlign = Paint.Align.CENTER
            }

            canvas.drawText("✦ ﷽ ✦", targetWidth / 2f, 150f, ornamentPaint)
            canvas.drawText("HAYIRLI CUMALAR", targetWidth / 2f, 225f, titlePaint)

            // Decorative horizontal gold divider
            val divY = 265f
            canvas.drawLine(targetWidth / 2f - 180f, divY, targetWidth / 2f + 180f, divY, goldPaint)
            canvas.drawCircle(targetWidth / 2f, divY, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#FFF3D0")
            })

            // Prepared personalized text
            var processedText = messageText
            if (recipientName.isNotBlank()) {
                processedText = processedText.replace("{isim}", recipientName)
            } else {
                processedText = processedText.replace("{isim}", "")
            }

            // Message text layout
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textSize = 42f
                typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
                setShadowLayer(6f, 0f, 3f, android.graphics.Color.BLACK)
            }

            val textWidth = (targetWidth - (frameMargin * 2) - 80).toInt()
            val staticLayout = StaticLayout.Builder.obtain(
                processedText,
                0,
                processedText.length,
                textPaint,
                textWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(10f, 1.25f)
                .build()

            // Calculate vertical center in bottom area
            val availableHeight = targetHeight - 320f - 180f
            val textTopY = 320f + (availableHeight - staticLayout.height) / 2f

            canvas.save()
            canvas.translate((targetWidth - textWidth) / 2f, textTopY)
            staticLayout.draw(canvas)
            canvas.restore()

            // Signature at bottom
            val signatureText = when {
                signature.isNotBlank() -> signature
                recipientName.isNotBlank() -> "Selam ve Dua İle..."
                else -> "Dualarınızın Kabul Olması Dileğiyle"
            }
            val signPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.parseColor("#E5BE65")
                textSize = 34f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
                textAlign = Paint.Align.CENTER
                setShadowLayer(4f, 0f, 2f, android.graphics.Color.BLACK)
            }
            canvas.drawText(signatureText, targetWidth / 2f, targetHeight - 100f, signPaint)

            // Save to FileProvider cache file
            val sharedDir = File(context.cacheDir, "shared")
            if (!sharedDir.exists()) {
                sharedDir.mkdirs()
            }
            val cardFile = File(sharedDir, "cuma_kart_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(cardFile)
            cardBitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
            outputStream.flush()
            outputStream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cardFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadBitmap(context: Context, keyOrPath: String): Bitmap {
        return try {
            when (keyOrPath) {
                "mosque_sunset" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                "mosque_spiritual" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_spiritual)
                else -> {
                    if (keyOrPath.startsWith("/")) {
                        val file = File(keyOrPath)
                        if (file.exists()) {
                            BitmapFactory.decodeFile(file.absolutePath)
                                ?: BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                        } else {
                            BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                        }
                    } else {
                        BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback gradient solid bitmap
            val fallback = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888)
            val c = Canvas(fallback)
            c.drawColor(android.graphics.Color.parseColor("#004D36"))
            fallback
        }
    }
}
