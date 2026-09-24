package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

object WhatsAppSender {
    const val PACKAGE_WHATSAPP = "com.whatsapp"
    const val PACKAGE_WHATSAPP_BUSINESS = "com.whatsapp.w4b"

    fun isWhatsAppInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(PACKAGE_WHATSAPP, PackageManager.GET_ACTIVITIES)
            true
        } catch (_: Exception) {
            try {
                pm.getPackageInfo(PACKAGE_WHATSAPP_BUSINESS, PackageManager.GET_ACTIVITIES)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun cleanPhoneNumber(phone: String): String {
        var cleaned = phone.replace(Regex("[^0-9+]"), "")
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1)
        } else if (cleaned.startsWith("0")) {
            // Default Turkish number assumption if starts with 0
            cleaned = "90" + cleaned.substring(1)
        } else if (!cleaned.startsWith("90") && cleaned.length == 10) {
            cleaned = "90$cleaned"
        }
        return cleaned
    }

    /**
     * Prepares an Intent to send a message (and optional image) directly to a contact's WhatsApp.
     */
    fun createDirectSendIntent(
        context: Context,
        phoneNumber: String,
        message: String,
        imageUri: Uri? = null
    ): Intent {
        val cleanNumber = cleanPhoneNumber(phoneNumber)
        
        return if (imageUri != null) {
            // Sharing image with caption to WhatsApp
            Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra("jid", "$cleanNumber@s.whatsapp.net")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                
                // Try target WhatsApp specifically
                val targetPkg = when {
                    isPackageInstalled(context, PACKAGE_WHATSAPP) -> PACKAGE_WHATSAPP
                    isPackageInstalled(context, PACKAGE_WHATSAPP_BUSINESS) -> PACKAGE_WHATSAPP_BUSINESS
                    else -> null
                }
                if (targetPkg != null) {
                    setPackage(targetPkg)
                }
            }
        } else {
            // Text only direct chat intent
            val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val targetPkg = when {
                isPackageInstalled(context, PACKAGE_WHATSAPP) -> PACKAGE_WHATSAPP
                isPackageInstalled(context, PACKAGE_WHATSAPP_BUSINESS) -> PACKAGE_WHATSAPP_BUSINESS
                else -> null
            }
            if (targetPkg != null) {
                intent.setPackage(targetPkg)
            }
            intent
        }
    }

    fun launchDirectSend(
        context: Context,
        phoneNumber: String,
        message: String,
        imageUri: Uri? = null
    ): Boolean {
        return try {
            val intent = createDirectSendIntent(context, phoneNumber, message, imageUri)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to web browser WhatsApp link
            try {
                val cleanNumber = cleanPhoneNumber(phoneNumber)
                val fallbackUrl = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
                true
            } catch (err: Exception) {
                Toast.makeText(context, "WhatsApp açılamadı: ${err.message}", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    /**
     * Standard share sheet to WhatsApp status or groups
     */
    fun shareGeneralToWhatsApp(
        context: Context,
        message: String,
        imageUri: Uri? = null
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
                
                val targetPkg = when {
                    isPackageInstalled(context, PACKAGE_WHATSAPP) -> PACKAGE_WHATSAPP
                    isPackageInstalled(context, PACKAGE_WHATSAPP_BUSINESS) -> PACKAGE_WHATSAPP_BUSINESS
                    else -> null
                }
                if (targetPkg != null) {
                    setPackage(targetPkg)
                }
            }
            context.startActivity(Intent.createChooser(intent, "Cuma Mesajını Paylaş"))
        } catch (e: Exception) {
            Toast.makeText(context, "Paylaşım hatası: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }
}
