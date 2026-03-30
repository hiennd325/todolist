package com.example.todolist.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todolist.MainActivity
import com.example.todolist.R

object NotificationHelper {

    fun showNotification(
        context: Context,
        todoId: Int,
        title: String,
        description: String,
        actions: List<NotificationAction> = emptyList()
    ) {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("todo_id", todoId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            todoId,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description.ifEmpty { "Task reminder" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Add action buttons
        actions.forEach { action ->
            val actionIntent = createActionIntent(context, todoId, action)
            builder.addAction(action.icon, action.label, actionIntent)
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(todoId, builder.build())
    }

    private fun createActionIntent(
        context: Context,
        todoId: Int,
        action: NotificationAction
    ): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            putExtra("todo_id", todoId)
            putExtra("action", action.type.name)
        }
        return PendingIntent.getBroadcast(
            context,
            todoId * 100 + action.type.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}

enum class NotificationActionType {
    COMPLETE, SNOOZE, DISMISS
}

data class NotificationAction(
    val type: NotificationActionType,
    val label: String,
    val icon: Int
)
