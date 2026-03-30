package com.example.todolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todo_id", 0)
        val todoTitle = intent.getStringExtra("todo_title") ?: "Task Reminder"
        val todoDescription = intent.getStringExtra("todo_description") ?: ""

        // Create action buttons
        val actions = listOf(
            NotificationAction(
                type = NotificationActionType.COMPLETE,
                label = "Complete",
                icon = android.R.drawable.checkbox_on_background
            ),
            NotificationAction(
                type = NotificationActionType.SNOOZE,
                label = "Snooze 15min",
                icon = android.R.drawable.ic_menu_recent_history
            )
        )

        NotificationHelper.showNotification(
            context = context,
            todoId = todoId,
            title = todoTitle,
            description = todoDescription,
            actions = actions
        )
    }
}
