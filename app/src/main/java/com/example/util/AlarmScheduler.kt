package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.receiver.FridayAlarmReceiver
import java.util.Calendar
import java.util.Locale

object AlarmScheduler {
    const val REQUEST_CODE = 1001

    fun calculateNextFridayMillis(targetHour: Int, targetMinute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        // Set to desired hour and minute
        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Adjust to Friday
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysUntilFriday = if (currentDayOfWeek == Calendar.FRIDAY) {
            if (candidate.timeInMillis > now) 0 else 7
        } else if (currentDayOfWeek < Calendar.FRIDAY) {
            Calendar.FRIDAY - currentDayOfWeek
        } else {
            // Saturday (7) -> next Friday is 6 days away
            (7 - currentDayOfWeek) + Calendar.FRIDAY
        }

        candidate.add(Calendar.DAY_OF_YEAR, daysUntilFriday)
        return candidate.timeInMillis
    }

    fun scheduleFridayReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val nextFridayMillis = calculateNextFridayMillis(hour, minute)

        val intent = Intent(context, FridayAlarmReceiver::class.java).apply {
            action = "com.aistudio.cumamesaj.ACTION_FRIDAY_ALARM"
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextFridayMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextFridayMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // If exact alarm permission is missing, fall back to normal set
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                nextFridayMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, FridayAlarmReceiver::class.java).apply {
            action = "com.aistudio.cumamesaj.ACTION_FRIDAY_ALARM"
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
        alarmManager.cancel(pendingIntent)
    }

    fun formatRemainingTime(targetMillis: Long): String {
        val diff = targetMillis - System.currentTimeMillis()
        if (diff <= 0) return "Şimdi vaktidir!"

        val totalMinutes = diff / (1000 * 60)
        val hours = totalMinutes / 60
        val days = hours / 24
        val remainingHours = hours % 24
        val remainingMins = totalMinutes % 60

        return when {
            days > 0 -> "$days gün, $remainingHours saat kaldı"
            hours > 0 -> "$hours saat, $remainingMins dakika kaldı"
            else -> "$remainingMins dakika kaldı"
        }
    }

    fun formatTime(hour: Int, minute: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }
}
