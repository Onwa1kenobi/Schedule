package io.julius.schedule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Schedules")
class Schedule(
    @PrimaryKey
    val id: Int,
    var description: String,
    var timeInMillis: Long = 0L,
    var dayOfMonth: Int,
    var month: Int,
    var year: Int
)