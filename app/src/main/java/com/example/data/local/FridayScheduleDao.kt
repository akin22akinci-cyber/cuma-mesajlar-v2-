package com.example.data.local

import androidx.room.*
import com.example.data.model.FridaySchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface FridayScheduleDao {
    @Query("SELECT * FROM friday_schedule WHERE id = 1")
    fun getScheduleFlow(): Flow<FridaySchedule?>

    @Query("SELECT * FROM friday_schedule WHERE id = 1")
    suspend fun getSchedule(): FridaySchedule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSchedule(schedule: FridaySchedule)

    @Update
    suspend fun updateSchedule(schedule: FridaySchedule)
}
