package com.example.data.local

import androidx.room.*
import com.example.data.model.FridayMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface FridayMessageDao {
    @Query("SELECT * FROM friday_messages ORDER BY isFavorite DESC, id ASC")
    fun getAllMessages(): Flow<List<FridayMessage>>

    @Query("SELECT * FROM friday_messages WHERE category = :category ORDER BY id ASC")
    fun getMessagesByCategory(category: String): Flow<List<FridayMessage>>

    @Query("SELECT * FROM friday_messages WHERE id = :id")
    suspend fun getMessageById(id: Long): FridayMessage?

    @Query("SELECT * FROM friday_messages LIMIT 1")
    suspend fun getFirstMessage(): FridayMessage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: FridayMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<FridayMessage>)

    @Update
    suspend fun updateMessage(message: FridayMessage)

    @Query("UPDATE friday_messages SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Delete
    suspend fun deleteMessage(message: FridayMessage)

    @Query("SELECT COUNT(*) FROM friday_messages")
    suspend fun getMessageCount(): Int
}
