package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FridayMessage
import com.example.data.model.FridaySchedule
import com.example.data.model.RecipientContact
import com.example.data.model.SentLog

@Database(
    entities = [
        RecipientContact::class,
        FridayMessage::class,
        FridaySchedule::class,
        SentLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CumaDatabase : RoomDatabase() {
    abstract fun recipientDao(): RecipientDao
    abstract fun fridayMessageDao(): FridayMessageDao
    abstract fun fridayScheduleDao(): FridayScheduleDao
    abstract fun sentLogDao(): SentLogDao

    companion object {
        @Volatile
        private var INSTANCE: CumaDatabase? = null

        fun getDatabase(context: Context): CumaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CumaDatabase::class.java,
                    "cuma_mesajlari_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
