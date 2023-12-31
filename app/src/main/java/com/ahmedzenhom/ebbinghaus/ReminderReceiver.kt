package com.ahmedzenhom.ebbinghaus

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsModel
import com.ahmedzenhom.ebbinghaus.data.repository.EventRepository
import com.ahmedzenhom.ebbinghaus.ui.main_screen.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("UnspecifiedImmutableFlag")
    companion object {

        private const val EVENT_NAME = "event_name"
        private const val REMINDER_ID = "reminder_id"
        private const val REMINDER_TEXT = "reminder_text"

        private fun getReminderIntent(
            context: Context,
            event: EventModel,
            reminder: EventSlotsModel
        ): Intent {
            val reminderText = event.description
            val eventName = "${event.title} - ${
                context.getString(R.string.reminder_no, reminder.order + 1)
            }"

            return Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EVENT_NAME, eventName)
                putExtra(REMINDER_ID, reminder.id)
                putExtra(REMINDER_TEXT, reminderText)
            }
        }

        fun scheduleReminders(context: Context, event: EventModel) {
            event.slots.forEach { reminder ->
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminder.id,
                    getReminderIntent(context, event, reminder),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.slotTime,
                    pendingIntent
                )
            }
        }

        fun cancelReminders(context: Context, event: EventModel) {
            event.slots.forEach { reminder ->
                val reminderId = reminder.id
                // Cancel Notification if shown
                val notificationManager =
                    context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(reminderId)
                // Cancel Alarm
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                val reminderPendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminderId,
                    getReminderIntent(context, event, reminder),
                    PendingIntent.FLAG_NO_CREATE
                )
                if (reminderPendingIntent != null) {
                    alarmManager.cancel(reminderPendingIntent)
                    reminderPendingIntent.cancel()
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag", "UseCompatLoadingForDrawables")
    override fun onReceive(context: Context?, intent: Intent?) {
        // Notification channel configs
        val notificationManager =
            context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminders_channel"
        val channelName = "Ebbinghaus Reminders Channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        // Event reminder data
        val eventName = intent?.getStringExtra("event_name")
        val reminderId = intent?.getIntExtra("reminder_id", 0) ?: 0
        val reminderText = intent?.getStringExtra("reminder_text")

        // Pending intent for main activity
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Building a notification
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(eventName)
            .setContentText(reminderText)
            .setSmallIcon(R.mipmap.ic_notification)
            .setLargeIcon(context.getDrawable(R.mipmap.ic_notification)?.toBitmap())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Showing the notification
        notificationManager.notify(reminderId, notificationBuilder.build())
    }
}

@AndroidEntryPoint
class OnBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: EventRepository

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null)
            return
        GlobalScope.launch {
            val events = repository.getAllEventsWithSlotsAfter(Calendar.getInstance().timeInMillis)
            events.forEach { ReminderReceiver.scheduleReminders(context, it) }
        }
    }
}