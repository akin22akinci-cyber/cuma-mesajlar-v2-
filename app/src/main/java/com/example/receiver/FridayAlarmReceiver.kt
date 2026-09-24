package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.CumaDatabase
import com.example.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FridayAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "cuma_reminders"
        const val NOTIFICATION_ID = 2001
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = CumaDatabase.getDatabase(context)
                val schedule = db.fridayScheduleDao().getSchedule()

                if (schedule != null && schedule.isEnabled) {
                    showFridayNotification(context)

                    // Re-schedule for next Friday
                    AlarmScheduler.scheduleFridayReminder(context, schedule.hour, schedule.minute)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showFridayNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cuma Hatırlatıcı",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Cuma günleri mesaj gönderme zamanı hatırlatıcısı"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open app and trigger send flow
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_START_SEND_FLOW", true)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val contentPendingIntent = PendingIntent.getActivity(context, 0, openIntent, flags)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_fg)
            .setContentTitle("Mübarek Cuma Vakti Geldi! 🕌")
            .setContentText("Hazırladığınız Cuma mesajını rehberinizdeki sevdiklerinize WhatsApp ile iletin.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Mübarek Cuma gününün feyzi ve bereketi üzerinize olsun. Seçtiğiniz kişilere resimli veya dualı Cuma mesajınızı göndermek için dokunun."
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .addAction(
                R.drawable.ic_launcher_fg,
                "Şimdi Gönder",
                contentPendingIntent
            )
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
