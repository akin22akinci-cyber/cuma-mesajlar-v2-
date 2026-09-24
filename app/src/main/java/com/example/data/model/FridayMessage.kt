package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friday_messages")
data class FridayMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "Ayet & Hadis", "Dualar & Bereket", "Samimi & Kısa", "Akraba & Büyükler", "Özel"
    val isFavorite: Boolean = false,
    val isCustom: Boolean = false
)
