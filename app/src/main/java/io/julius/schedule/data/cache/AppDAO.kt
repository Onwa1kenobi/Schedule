package io.julius.schedule.data.cache

import androidx.lifecycle.LiveData
import androidx.room.*
import io.julius.schedule.data.model.Schedule

@Dao
interface AppDAO {

    @Query("SELECT * FROM Schedules ORDER BY timeInMillis")
    fun getSchedules(): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedules WHERE dayOfMonth = :dayOfMonth AND month = :month AND year = :year ORDER BY timeInMillis")
    fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>>

    @Query("SELECT * FROM Schedules WHERE id = :id")
    fun getSchedule(id: Int): LiveData<Schedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: Schedule): Long

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
}