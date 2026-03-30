package com.example.todolist.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.todolist.data.model.TodoItem
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "todo_reminders"
        const val CHANNEL_NAME = "Task Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for task reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(todo: TodoItem) {
        val reminderTime = todo.reminderTime ?: return

        // Don't schedule if reminder time is in the past
        if (reminderTime <= System.currentTimeMillis()) {
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_description", todo.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(todoId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleRecurringReminder(todo: TodoItem) {
        val recurrenceType = todo.recurrenceType
        if (recurrenceType == com.example.todolist.data.model.RecurrenceType.NONE) {
            return
        }

        val interval = when (recurrenceType) {
            com.example.todolist.data.model.RecurrenceType.DAILY -> TimeUnit.DAYS.toMillis(1)
            com.example.todolist.data.model.RecurrenceType.WEEKLY -> TimeUnit.DAYS.toMillis(7)
            com.example.todolist.data.model.RecurrenceType.MONTHLY -> TimeUnit.DAYS.toMillis(30)
            com.example.todolist.data.model.RecurrenceType.YEARLY -> TimeUnit.DAYS.toMillis(365)
            com.example.todolist.data.model.RecurrenceType.NONE -> return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("todo_id", todo.id)
            putExtra("todo_title", todo.title)
            putExtra("todo_description", todo.description)
            putExtra("is_recurring", true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todo.id + 10000, // Different ID for recurring
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule repeating alarm
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            todo.reminderTime ?: System.currentTimeMillis(),
            interval,
            pendingIntent
        )
    }

    fun scheduleMultipleReminders(todo: TodoItem, reminderTimes: List<Long>) {
        reminderTimes.forEachIndexed { index, reminderTime ->
            if (reminderTime > System.currentTimeMillis()) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                    putExtra("todo_id", todo.id)
                    putExtra("todo_title", todo.title)
                    putExtra("todo_description", todo.description)
                    putExtra("reminder_index", index)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    todo.id * 1000 + index,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                }
            }
        }
    }

    fun cancelAllReminders(todoId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Cancel main reminder
        val mainIntent = Intent(context, ReminderBroadcastReceiver::class.java)
        val mainPendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(mainPendingIntent)

        // Cancel recurring reminder
        val recurringIntent = Intent(context, ReminderBroadcastReceiver::class.java)
        val recurringPendingIntent = PendingIntent.getBroadcast(
            context,
            todoId + 10000,
            recurringIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(recurringPendingIntent)
    }
}
