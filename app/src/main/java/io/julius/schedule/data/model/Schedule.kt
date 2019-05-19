package io.julius.schedule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var description: String,
    var timeInMillis: Long,
    var dayOfMonth: Int,
    var month: Int,
    var year: Int
)