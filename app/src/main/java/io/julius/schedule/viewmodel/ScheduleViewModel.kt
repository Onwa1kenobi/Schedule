package io.julius.schedule.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.julius.schedule.ScheduleViewContract
import io.julius.schedule.data.ScheduleRepository
import io.julius.schedule.data.model.Schedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    // LiveData object for view state interaction
    val scheduleViewContract: MutableLiveData<Event<ScheduleViewContract>> = MutableLiveData()

    // Mediator livedata that sits between the database retrieved livedata object and the observing view
    val schedules = MediatorLiveData<List<Schedule>>()

    fun getSchedulesForDate(dayOfMonth: Int, month: Int, year: Int) {

        val job = async {
            repository.getDateSchedules(dayOfMonth, month, year)
        }

        launch(Dispatchers.Main) {
            // Subscribe to repository fetched schedules
            schedules.addSource(job.await()) {
                schedules.postValue(it)
            }
        }
    }

    fun saveSchedule(description: String, calendar: Calendar) {
        val schedule = Schedule(
            description = description,
            timeInMillis = calendar.timeInMillis,
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH),
            year = calendar.get(Calendar.YEAR)
        )

        launch {
            val saved = repository.saveSchedule(schedule)
            if (saved) {
                // Successfully saved. return to schedules
                scheduleViewContract.postValue(Event(ScheduleViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                scheduleViewContract.postValue(Event(ScheduleViewContract.MessageDisplay("Failed to save schedule")))
            }
        }
    }

    fun editSchedule(schedule: Schedule, description: String, calendar: Calendar) {
        schedule.apply {
            this.description = description
            timeInMillis = calendar.timeInMillis
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
        }

        launch {
            val saved = repository.saveSchedule(schedule)
            if (saved) {
                // Successfully saved. return to schedules
                scheduleViewContract.postValue(Event(ScheduleViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                scheduleViewContract.postValue(Event(ScheduleViewContract.MessageDisplay("Failed to save schedule")))
            }
        }
    }

    fun editSchedule(schedule: Schedule) {
        // Navigate to edit fragment
        scheduleViewContract.postValue(Event(ScheduleViewContract.NavigateToEditSchedule(schedule)))
    }

    fun deleteSchedule(schedule: Schedule) {
        launch {
            repository.deleteSchedule(schedule)
        }
    }
}