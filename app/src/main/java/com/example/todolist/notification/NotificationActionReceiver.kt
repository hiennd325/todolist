package com.example.todolist.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todo_id", 0)
        val actionName = intent.getStringExtra("action") ?: return
        val action = NotificationActionType.valueOf(actionName)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TodoDatabase.getDatabase(context)
                val repository = TodoRepository(database.todoDao())
                val scheduler = NotificationScheduler(context)

                when (action) {
                    NotificationActionType.COMPLETE -> {
                        val todo = repository.getTodoById(todoId).first()
                        todo?.let {
                            repository.update(it.copy(isCompleted = true))
                        }
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(todoId)
                    }
                    NotificationActionType.SNOOZE -> {
                        val todo = repository.getTodoById(todoId).first()
                        todo?.let {
                            val snoozeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15)
                            scheduler.scheduleReminder(it.copy(reminderTime = snoozeTime))
                        }
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(todoId)
                    }
                    NotificationActionType.DISMISS -> {
                        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancel(todoId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
