package io.julius.schedule.data

import androidx.lifecycle.LiveData
import io.julius.schedule.data.cache.LocalDataSource
import io.julius.schedule.data.model.Schedule

/**
 * Provides an implementation of the [ScheduleRepository] interface for communicating to and from
 * Data sources
 */
class ScheduleRepositoryImpl(private val localDataSource: LocalDataSource) : ScheduleRepository {

    override suspend fun getSchedules(): LiveData<List<Schedule>> {
        return localDataSource.getSchedules()
    }

    override suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>> {
        return localDataSource.getDateSchedules(dayOfMonth, month, year)
    }

    override suspend fun getSchedule(id: Int): LiveData<Schedule> {
        return localDataSource.getSchedule(id)
    }

    override suspend fun saveSchedule(schedule: Schedule): Long {
        return localDataSource.saveSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        localDataSource.deleteSchedule(schedule)
    }
}