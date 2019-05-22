package io.julius.schedule

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import io.julius.schedule.data.ScheduleRepositoryImpl
import io.julius.schedule.data.cache.AppDatabase
import io.julius.schedule.data.cache.LocalDataSource
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext


class AlarmReceiver : BroadcastReceiver(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Log.e("SCHEDULE", "Beginning")

        val repository =
            ScheduleRepositoryImpl(LocalDataSource(AppDatabase.getInstance(context.applicationContext).appDao()))

        if (intent.action != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

                val alarmManager = AlarmManager(context)

                val job = GlobalScope.async {
                    repository.getSchedules()
                }

                GlobalScope.launch(Dispatchers.Main) {
                    val schedules = job.await().value
                    schedules?.forEach {
                        if (it.timeInMillis > Calendar.getInstance().timeInMillis) {
                            // Re-register the alarms
                            alarmManager.registerAlarm(it)
                        }
                    }
                }

                return
            }
        }

        Log.e("SCHEDULE", "It came here")
        // Alarm received, fetch the schedule and trigger the notification

        launch {
            val data = repository.getSchedule(intent.getIntExtra("id", 1))

            data.observeForever { schedule ->
                Log.e("SCHEDULE", "E mella")

                showNotification(
                    context,
                    MainActivity::class.java,
                    "Schedule",
                    schedule.description,
                    schedule.id
                )
            }

//            val schedule = data.value!!

            Log.e("SCHEDULE", "E balleh")

//            showNotification(
//                context,
//                MainActivity::class.java,
//                "Schedule",
//                schedule.description,
//                schedule.id
//            )
        }

//        runBlocking {
//            val schedule = async(Dispatchers.IO) {
//                repository.getSchedule(intent.getIntExtra("id", 1))
//            }.await().value!!
//
//            showNotification(
//                context,
//                MainActivity::class.java,
//                "Schedule",
//                schedule.description,
//                schedule.id
//            )
//        }
    }

    private fun showNotification(context: Context, cls: Class<*>, title: String, content: String, requestCode: Int) {
        Log.e("SCHEDULE", "Show notification called")
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationIntent = Intent(context, cls)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(cls)
        stackBuilder.addNextIntent(notificationIntent)

        val pendingIntent =
            stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context)

        val notification = builder.setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestCode, notification)
    }
}
