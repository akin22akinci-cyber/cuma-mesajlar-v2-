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
        recipientName: String = "",
        fontStyle: String = "Serif",
        frameStyle: String = "Gold",
        textSizeModifier: Float = 1.0f,
        textColorHex: String = "#FFFFFF"
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // Load base bitmap (handles resources, internal files, and content:// URIs from photo picker)
            val baseBitmap: Bitmap = loadBitmap(context, imageKeyOrPath)

            // High resolution vertical card dimension (1080 x 1440)
            val targetWidth = 1080
            val targetHeight = 1440
            val cardBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(cardBitmap)

            // Fill base photo
            val srcRect = Rect(0, 0, baseBitmap.width, baseBitmap.height)
            val dstRect = Rect(0, 0, targetWidth, targetHeight)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(baseBitmap, srcRect, dstRect, paint)

            // Dynamic vignette based on frame/theme style
            val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, targetHeight * 0.20f,
                    0f, targetHeight.toFloat(),
                    intArrayOf(
                        android.graphics.Color.argb(20, 0, 0, 0),
                        android.graphics.Color.argb(170, 2, 22, 16),
                        android.graphics.Color.argb(235, 1, 12, 9),
                        android.graphics.Color.argb(250, 0, 8, 6)
                    ),
                    floatArrayOf(0f, 0.35f, 0.75f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), gradientPaint)

            // Top soft vignette for contrast
            val topVignette = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, 0f, 260f,
                    intArrayOf(
                        android.graphics.Color.argb(180, 0, 16, 11),
                        android.graphics.Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, targetWidth.toFloat(), 260f, topVignette)

            val frameMargin = 44f
            val frameColor = when (frameStyle) {
                "Emerald" -> android.graphics.Color.parseColor("#38D39F")
                "Minimal" -> android.graphics.Color.parseColor("#40FFFFFF")
                "Lantern" -> android.graphics.Color.parseColor("#FFD54F")
                else -> android.graphics.Color.parseColor("#E5BE65") // Gold
            }

            if (frameStyle != "Minimal") {
                // Outer ornate border
                val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = frameColor
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                canvas.drawRect(
                    frameMargin, frameMargin,
                    targetWidth - frameMargin, targetHeight - frameMargin,
                    outerPaint
                )

                // Corner ornamental accents
                drawCornerAccents(canvas, frameMargin, targetWidth.toFloat(), targetHeight.toFloat(), outerPaint)

                // Inner thin line
                val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.argb(120, android.graphics.Color.red(frameColor), android.graphics.Color.green(frameColor), android.graphics.Color.blue(frameColor))
                    style = Paint.Style.STROKE
                    strokeWidth = 1.5f
                }
                canvas.drawRect(
                    frameMargin + 12f, frameMargin + 12f,
                    targetWidth - frameMargin - 12f, targetHeight - frameMargin - 12f,
                    innerPaint
                )
            }

            // Typeface based on user's choice
            val typeface = when (fontStyle) {
                "Sans" -> Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                "Script" -> Typeface.create(Typeface.SERIF, Typeface.ITALIC)
                "Bold" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
                else -> Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            }

            // Header "HAYIRLI CUMALAR"
            val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = when (frameStyle) {
                    "Emerald" -> android.graphics.Color.parseColor("#E8FFF5")
                    else -> android.graphics.Color.parseColor("#FFF3D0")
                }
                textSize = 56f
                this.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                setShadowLayer(8f, 0f, 4f, android.graphics.Color.BLACK)
            }

            val ornamentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = frameColor
                textSize = 36f
                textAlign = Paint.Align.CENTER
            }

            val ornamentSymbol = when (frameStyle) {
                "Lantern" -> "✨ ﷽ ✨"
                "Emerald" -> "🌿 ﷽ 🌿"
                else -> "✦ ﷽ ✦"
            }

            canvas.drawText(ornamentSymbol, targetWidth / 2f, 135f, ornamentPaint)
            canvas.drawText("HAYIRLI CUMALAR", targetWidth / 2f, 215f, titlePaint)

            // Divider
            val divY = 255f
            val divPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = frameColor
                strokeWidth = 3f
            }
            canvas.drawLine(targetWidth / 2f - 180f, divY, targetWidth / 2f + 180f, divY, divPaint)
            canvas.drawCircle(targetWidth / 2f, divY, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
            })

            // Prepared text with name replacement
            var processedText = messageText
            if (recipientName.isNotBlank()) {
                processedText = processedText.replace("{isim}", recipientName)
            } else {
                processedText = processedText.replace("{isim}", "").trim()
            }

            // Message text paint
            val parsedTextColor = try {
                android.graphics.Color.parseColor(textColorHex)
            } catch (_: Exception) {
                android.graphics.Color.WHITE
            }

            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = parsedTextColor
                textSize = 42f * textSizeModifier
                this.typeface = typeface
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

            // Vertical center
            val availableHeight = targetHeight - 300f - 160f
            val textTopY = 300f + (availableHeight - staticLayout.height) / 2f

            canvas.save()
            canvas.translate((targetWidth - textWidth) / 2f, textTopY)
            staticLayout.draw(canvas)
            canvas.restore()

            // Signature at bottom
            val signatureText = when {
                signature.isNotBlank() -> signature
                recipientName.isNotBlank() -> "Selam ve Dua İle..."
                else -> "Dualarınızın Kabul Olması Dileğiyle 🤲"
            }
            val signPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                color = frameColor
                textSize = 34f
                this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
                textAlign = Paint.Align.CENTER
                setShadowLayer(5f, 0f, 2f, android.graphics.Color.BLACK)
            }
            canvas.drawText(signatureText, targetWidth / 2f, targetHeight - 90f, signPaint)

            // Cache File
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

    private fun drawCornerAccents(canvas: Canvas, margin: Float, width: Float, height: Float, paint: Paint) {
        val accentSize = 24f
        canvas.drawLine(margin, margin + accentSize, margin + accentSize, margin, paint)
        canvas.drawLine(width - margin - accentSize, margin, width - margin, margin + accentSize, paint)
        canvas.drawLine(margin, height - margin - accentSize, margin + accentSize, height - margin, paint)
        canvas.drawLine(width - margin - accentSize, height - margin, width - margin, height - margin - accentSize, paint)
    }

    private fun loadBitmap(context: Context, keyOrPath: String): Bitmap {
        return try {
            when {
                keyOrPath == "mosque_sunset" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                keyOrPath == "mosque_spiritual" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_spiritual)
                keyOrPath == "mosque_night" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_night)
                keyOrPath == "mosque_interior" -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_interior)
                keyOrPath.startsWith("content://") -> {
                    val uri = Uri.parse(keyOrPath)
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    } ?: BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                }
                keyOrPath.startsWith("/") -> {
                    val file = File(keyOrPath)
                    if (file.exists()) {
                        BitmapFactory.decodeFile(file.absolutePath)
                            ?: BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                    } else {
                        BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
                    }
                }
                else -> BitmapFactory.decodeResource(context.resources, R.drawable.img_mosque_sunset)
            }
        } catch (_: Exception) {
            val fallback = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888)
            val c = Canvas(fallback)
            c.drawColor(android.graphics.Color.parseColor("#004D36"))
            fallback
        }
    }
}
