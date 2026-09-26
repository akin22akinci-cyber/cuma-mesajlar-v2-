package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

enum class SocialPlatform(
    val id: String,
    val title: String,
    val packageName: String,
    val hexColor: String
) {
    WHATSAPP("whatsapp", "WhatsApp", "com.whatsapp", "#25D366"),
    TELEGRAM("telegram", "Telegram", "org.telegram.messenger", "#229ED9"),
    INSTAGRAM("instagram", "Instagram", "com.instagram.android", "#E4405F"),
    MESSENGER("messenger", "Messenger", "com.facebook.orca", "#0084FF"),
    FACEBOOK("facebook", "Facebook", "com.facebook.katana", "#1877F2"),
    SNAPCHAT("snapchat", "Snapchat", "com.snapchat.android", "#FFFC00"),
    UNIVERSAL("universal", "Diğer / Paylaş", "", "#006C4C")
}

object SocialShareManager {

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        if (packageName.isBlank()) return true
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun shareToPlatform(
        context: Context,
        platform: SocialPlatform,
        message: String,
        imageUri: Uri?
    ) {
        when (platform) {
            SocialPlatform.WHATSAPP -> {
                WhatsAppSender.shareGeneralToWhatsApp(context, message, imageUri)
            }
            SocialPlatform.TELEGRAM -> {
                shareDirectToPackage(context, "org.telegram.messenger", message, imageUri, "Telegram")
            }
            SocialPlatform.INSTAGRAM -> {
                shareToInstagram(context, message, imageUri)
            }
            SocialPlatform.MESSENGER -> {
                shareDirectToPackage(context, "com.facebook.orca", message, imageUri, "Messenger")
            }
            SocialPlatform.FACEBOOK -> {
                shareDirectToPackage(context, "com.facebook.katana", message, imageUri, "Facebook")
            }
            SocialPlatform.SNAPCHAT -> {
                shareDirectToPackage(context, "com.snapchat.android", message, imageUri, "Snapchat")
            }
            SocialPlatform.UNIVERSAL -> {
                shareUniversal(context, message, imageUri)
            }
        }
    }

    private fun shareDirectToPackage(
        context: Context,
        packageName: String,
        message: String,
        imageUri: Uri?,
        appName: String
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (imageUri != null) {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_TEXT, message)
                setPackage(packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            if (isAppInstalled(context, packageName)) {
                context.startActivity(intent)
            } else {
                // If specific app not installed, open chooser and notify
                Toast.makeText(context, "$appName yüklü değil, paylaşım menüsü açılıyor...", Toast.LENGTH_SHORT).show()
                shareUniversal(context, message, imageUri)
            }
        } catch (e: Exception) {
            shareUniversal(context, message, imageUri)
        }
    }

    private fun shareToInstagram(context: Context, message: String, imageUri: Uri?) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (imageUri != null) {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_TEXT, message)
                setPackage("com.instagram.android")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (isAppInstalled(context, "com.instagram.android")) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Instagram yüklü değil, paylaşım menüsü açılıyor...", Toast.LENGTH_SHORT).show()
                shareUniversal(context, message, imageUri)
            }
        } catch (_: Exception) {
            shareUniversal(context, message, imageUri)
        }
    }

    fun shareUniversal(context: Context, message: String, imageUri: Uri?) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                if (imageUri != null) {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    type = "text/plain"
                }
                putExtra(Intent.EXTRA_TEXT, message)
            }
            val chooser = Intent.createChooser(intent, "Cuma Tebrik Kartını Paylaş").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Paylaşım hatası: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
