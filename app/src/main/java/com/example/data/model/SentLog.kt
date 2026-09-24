package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sent_logs")
data class SentLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val contactName: String,
    val phoneNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageSnippet: String,
    val isSuccess: Boolean = true
)
