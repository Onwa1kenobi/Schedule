package io.julius.schedule.data

import androidx.lifecycle.LiveData
import io.julius.schedule.data.model.Schedule

interface ScheduleRepository {

    /**
     * Gets all the registered schedules from the local database and the remote database
     */
    suspend fun getSchedules(): LiveData<List<Schedule>>

    /**
     * Gets all the schedules registered for a particular date from the local database and the remote database
     */
    suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>>

    /**
     * Saves a schedule to the local database
     */
    suspend fun saveSchedule(schedule: Schedule): Boolean

    /**
     * Deletes a schedule from the local database
     */
    suspend fun deleteSchedule(schedule: Schedule)
}