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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.data.export.ExportImportManager
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.CustomCategoryRepository
import com.example.todolist.data.repository.StatisticsRepository
import com.example.todolist.data.repository.SubtaskRepository
import com.example.todolist.data.repository.TaskListRepository
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.data.settings.SettingsManager
import com.example.todolist.notification.NotificationScheduler
import com.example.todolist.ui.navigation.AppNavigation
import com.example.todolist.ui.screens.SettingsScreen
import com.example.todolist.ui.screens.SettingsViewModel
import com.example.todolist.ui.theme.TodolistTheme
import com.example.todolist.ui.viewmodel.CalendarViewModel
import com.example.todolist.ui.viewmodel.StatisticsViewModel
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
        val statisticsRepository = StatisticsRepository(database.todoDao())
        val customCategoryRepository = CustomCategoryRepository(database.customCategoryDao())

        notificationScheduler = NotificationScheduler(this)
        exportImportManager = ExportImportManager(this, repository, taskListRepository, subtaskRepository)

        val settingsManager = SettingsManager(this)

        val todoViewModel: TodoViewModel = ViewModelProvider(
            this,
            TodoViewModel.Factory(repository, taskListRepository, subtaskRepository)
        )[TodoViewModel::class.java]

        val statisticsViewModel: StatisticsViewModel = ViewModelProvider(
            this,
            StatisticsViewModel.Factory(statisticsRepository, repository)
        )[StatisticsViewModel::class.java]

        val calendarViewModel: CalendarViewModel = ViewModelProvider(
            this,
            CalendarViewModel.Factory(repository)
        )[CalendarViewModel::class.java]

        val settingsViewModel: SettingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.Factory(settingsManager, customCategoryRepository, exportImportManager)
        )[SettingsViewModel::class.java]

        // Request notification permission for Android 13+
        askNotificationPermission()

        setContent {
            val systemInDarkTheme = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }
            var currentScreen by remember { mutableStateOf("main") }

            val settings by settingsManager.settingsFlow.collectAsState(
                initial = SettingsManager.Settings(
                    themePreset = com.example.todolist.ui.theme.ThemePreset.Default,
                    darkMode = "system",
                    dynamicColor = false,
                    fontSize = "medium"
                )
            )

            val effectiveDarkTheme = when (settings.darkMode) {
                "light" -> false
                "dark" -> true
                else -> isDarkTheme
            }

            val customPrimaryColor = settings.customPrimaryColor?.let {
                try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { null }
            }
            val customPrimaryDarkColor = settings.customPrimaryDarkColor?.let {
                try { Color(android.graphics.Color.parseColor(it)) } catch (_: Exception) { null }
            }

            TodolistTheme(
                darkTheme = effectiveDarkTheme,
                dynamicColor = settings.dynamicColor,
                fontSize = settings.fontSize,
                customPrimaryColor = customPrimaryColor,
                customPrimaryDarkColor = customPrimaryDarkColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        "settings" -> SettingsScreen(
                            viewModel = settingsViewModel,
                            onNavigateBack = { currentScreen = "main" }
                        )
                        else -> AppNavigation(
                            todoViewModel = todoViewModel,
                            statisticsViewModel = statisticsViewModel,
                            calendarViewModel = calendarViewModel,
                            notificationScheduler = notificationScheduler,
                            exportImportManager = exportImportManager,
                            isDarkTheme = effectiveDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            onNavigateToSettings = { currentScreen = "settings" }
                        )
                    }
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
