package io.julius.schedule.data.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import io.julius.schedule.data.model.Schedule
import java.time.Year

@Dao
interface AppDAO {

    @Query("SELECT * FROM Schedules ORDER BY timeInMillis")
    suspend fun getSchedules(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedules where dayOfMonth = :dayOfMonth AND month = :month AND year = :year ORDER BY timeInMillis")
    suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: Schedule): Long

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
}