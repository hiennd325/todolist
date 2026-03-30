package com.example.todolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all reminders after device reboot
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = TodoDatabase.getDatabase(context)
                    val repository = TodoRepository(database.todoDao())
                    val scheduler = NotificationScheduler(context)

                    // Get all todos with reminders
                    val todos = repository.getTodosWithReminders(System.currentTimeMillis()).first()
                    todos.forEach { todo ->
                        scheduler.scheduleReminder(todo)
                        if (todo.recurrenceType != com.example.todolist.data.model.RecurrenceType.NONE) {
                            scheduler.scheduleRecurringReminder(todo)
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
}
