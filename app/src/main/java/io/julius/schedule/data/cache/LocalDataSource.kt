package io.julius.schedule.data.cache

import androidx.lifecycle.LiveData
import io.julius.schedule.data.DataSource
import io.julius.schedule.data.model.Schedule
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

class LocalDataSource(private val appDAO: AppDAO) : DataSource {

    override suspend fun getSchedules(): LiveData<List<Schedule>> {
        return appDAO.getSchedules()
    }

    override suspend fun getDateSchedules(dayOfMonth: Int, month: Int, year: Int): LiveData<List<Schedule>> {
        return appDAO.getDateSchedules(dayOfMonth, month, year)
    }

    override suspend fun saveSchedule(schedule: Schedule): Boolean {
        val rowId: Long? = appDAO.saveSchedule(schedule)
        return rowId != null
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        appDAO.deleteSchedule(schedule)
    }

}