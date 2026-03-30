package com.example.todolist.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TodoWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_REFRESH -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, TodoWidgetProvider::class.java)
                )
                onUpdate(context, appWidgetManager, ids)
            }
            ACTION_ADD_TASK -> {
                val launchIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra("open_add_dialog", true)
                }
                context.startActivity(launchIntent)
            }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.example.todolist.widget.REFRESH"
        const val ACTION_ADD_TASK = "com.example.todolist.widget.ADD_TASK"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_todo)

            // Set up click intent to open app
            val launchIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

            // Set up refresh button
            val refreshIntent = Intent(context, TodoWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)

            // Set up add task button
            val addIntent = Intent(context, TodoWidgetProvider::class.java).apply {
                action = ACTION_ADD_TASK
            }
            val addPendingIntent = PendingIntent.getBroadcast(
                context,
                2,
                addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_add_task, addPendingIntent)

            // Load tasks asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = TodoDatabase.getDatabase(context)
                    val repository = TodoRepository(database.todoDao())

                    val allTodos = repository.allTodos.first()
                    val pendingTodos = allTodos.filter { !it.isCompleted }.take(5)
                    val completedCount = allTodos.count { it.isCompleted }
                    val totalCount = allTodos.size

                    // Update widget content
                    views.setTextViewText(
                        R.id.widget_stats,
                        "$completedCount/$totalCount completed"
                    )

                    // Build task list string
                    val taskListText = if (pendingTodos.isEmpty()) {
                        "No pending tasks!"
                    } else {
                        pendingTodos.joinToString("\n") { "• ${it.title}" }
                    }
                    views.setTextViewText(R.id.widget_task_list, taskListText)

                    // Update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (e: Exception) {
                    views.setTextViewText(R.id.widget_task_list, "Error loading tasks")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
