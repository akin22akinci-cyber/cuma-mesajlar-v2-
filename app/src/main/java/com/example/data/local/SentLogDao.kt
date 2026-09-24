package com.example.data.local

import androidx.room.*
import com.example.data.model.SentLog
import kotlinx.coroutines.flow.Flow

@Dao
interface SentLogDao {
    @Query("SELECT * FROM sent_logs ORDER BY timestamp DESC LIMIT 100")
    fun getAllLogs(): Flow<List<SentLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SentLog)

    @Query("DELETE FROM sent_logs")
    suspend fun clearLogs()
}
