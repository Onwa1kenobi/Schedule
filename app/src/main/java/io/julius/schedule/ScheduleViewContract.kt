package io.julius.schedule

sealed class ScheduleViewContract {

    // Contract class to display message to the user
    class MessageDisplay(val message: String) : ScheduleViewContract()

    // Contract object to navigate back to list of schedules
    object SaveSuccess : ScheduleViewContract()
}