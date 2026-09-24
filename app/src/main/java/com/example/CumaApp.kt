package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.local.CumaDatabase
import com.example.data.repository.CumaRepository
import com.example.receiver.FridayAlarmReceiver
import com.example.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CumaApp : Application() {
    val database by lazy { CumaDatabase.getDatabase(this) }
    val repository by lazy { CumaRepository(database) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()

        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeDefaultsIfNeeded()
            val schedule = repository.getSchedule()
            if (schedule != null && schedule.isEnabled) {
                AlarmScheduler.scheduleFridayReminder(this@CumaApp, schedule.hour, schedule.minute)
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FridayAlarmReceiver.CHANNEL_ID,
                "Cuma Hatırlatıcı",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Cuma mesajı gönderme vaktinde bildirim verir"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
