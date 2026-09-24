package com.example.data.local

import androidx.room.*
import com.example.data.model.RecipientContact
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientDao {
    @Query("SELECT * FROM recipients ORDER BY name ASC")
    fun getAllRecipients(): Flow<List<RecipientContact>>

    @Query("SELECT * FROM recipients WHERE isSelected = 1 ORDER BY name ASC")
    fun getSelectedRecipients(): Flow<List<RecipientContact>>

    @Query("SELECT * FROM recipients WHERE isSelected = 1")
    suspend fun getSelectedRecipientsList(): List<RecipientContact>

    @Query("SELECT * FROM recipients WHERE id = :id")
    suspend fun getRecipientById(id: Long): RecipientContact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipient(recipient: RecipientContact): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipients(recipients: List<RecipientContact>)

    @Update
    suspend fun updateRecipient(recipient: RecipientContact)

    @Query("UPDATE recipients SET isSelected = :isSelected WHERE id = :id")
    suspend fun updateSelection(id: Long, isSelected: Boolean)

    @Query("UPDATE recipients SET isSelected = :selectAll")
    suspend fun updateAllSelection(selectAll: Boolean)

    @Query("UPDATE recipients SET lastSentTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastSent(id: Long, timestamp: Long)

    @Delete
    suspend fun deleteRecipient(recipient: RecipientContact)

    @Query("DELETE FROM recipients")
    suspend fun clearAll()
}
