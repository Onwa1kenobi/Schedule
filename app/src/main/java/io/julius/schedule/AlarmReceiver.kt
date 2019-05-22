package io.julius.schedule

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import io.julius.schedule.data.ScheduleRepositoryImpl
import io.julius.schedule.data.cache.AppDatabase
import io.julius.schedule.data.cache.LocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*


class AlarmReceiver : BroadcastReceiver(), CoroutineScope {

    override val coroutineContext = Dispatchers.IO

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        val repository =
            ScheduleRepositoryImpl(LocalDataSource(AppDatabase.getInstance(context.applicationContext).appDao()))

        if (intent.action != null) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

                val alarmManager = AlarmManager(context)

                val job = async {
                    repository.getSchedules()
                }

                launch(Dispatchers.Main) {
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

            // Alarm received, fetch the schedule and trigger the notification
            val job = async {
                repository.getSchedule(intent.getIntExtra("id", 1))
            }

            launch(Dispatchers.Main) {
                val schedule = job.await().value

                schedule?.let {
                    showNotification(context, MainActivity::class.java, "Schedule", schedule.description, schedule.id)
                }
            }
        }
    }

    private fun showNotification(context: Context, cls: Class<*>, title: String, content: String, requestCode: Int) {
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
            .setContentIntent(pendingIntent).build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestCode, notification)
    }
}
