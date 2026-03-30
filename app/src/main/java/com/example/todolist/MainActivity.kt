package com.example.todolist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.data.export.ExportImportManager
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.SubtaskRepository
import com.example.todolist.data.repository.TaskListRepository
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.notification.NotificationScheduler
import com.example.todolist.ui.screens.TodoListScreen
import com.example.todolist.ui.theme.TodolistTheme
import com.example.todolist.ui.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {

    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var exportImportManager: ExportImportManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, notifications will work
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TodoDatabase.getDatabase(this)
        val repository = TodoRepository(database.todoDao())
        val taskListRepository = TaskListRepository(database.taskListDao())
        val subtaskRepository = SubtaskRepository(database.subtaskDao())
        
        notificationScheduler = NotificationScheduler(this)
        exportImportManager = ExportImportManager(this, repository, taskListRepository, subtaskRepository)

        val viewModel: TodoViewModel = ViewModelProvider(
            this,
            TodoViewModel.Factory(repository, taskListRepository, subtaskRepository)
        )[TodoViewModel::class.java]

        // Request notification permission for Android 13+
        askNotificationPermission()

        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }

            TodolistTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TodoListScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme },
                        notificationScheduler = notificationScheduler,
                        exportImportManager = exportImportManager
                    )
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
