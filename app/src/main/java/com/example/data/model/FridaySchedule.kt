package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friday_schedule")
data class FridaySchedule(
    @PrimaryKey
    val id: Int = 1,
    val isEnabled: Boolean = true,
    val hour: Int = 9,
    val minute: Int = 0,
    val selectedMessageId: Long = 1,
    val selectedImageKey: String = "mosque_sunset", // "mosque_sunset", "mosque_spiritual", or custom URI
    val mergeTextOnImage: Boolean = true,
    val senderSignature: String = "",
    val autoRemindVibrate: Boolean = true
)
