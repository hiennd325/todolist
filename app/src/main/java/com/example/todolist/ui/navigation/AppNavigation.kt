package com.example.todolist.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.todolist.data.export.ExportImportManager
import com.example.todolist.notification.NotificationScheduler
import com.example.todolist.ui.screens.CalendarScreen
import com.example.todolist.ui.screens.StatisticsScreen
import com.example.todolist.ui.screens.TodoListScreen
import com.example.todolist.ui.viewmodel.CalendarViewModel
import com.example.todolist.ui.viewmodel.StatisticsViewModel
import com.example.todolist.ui.viewmodel.TodoViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Tasks : Screen("tasks", "Tasks", Icons.Default.Checklist)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.BarChart)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    todoViewModel: TodoViewModel,
    statisticsViewModel: StatisticsViewModel,
    calendarViewModel: CalendarViewModel,
    notificationScheduler: NotificationScheduler,
    exportImportManager: ExportImportManager,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Tasks, Screen.Statistics, Screen.Calendar, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (screen == Screen.Settings) {
                                onNavigateToSettings()
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tasks.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tasks.route) {
                TodoListScreen(
                    viewModel = todoViewModel,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme,
                    notificationScheduler = notificationScheduler,
                    exportImportManager = exportImportManager,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen(viewModel = statisticsViewModel)
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(viewModel = calendarViewModel)
            }

        }
    }
}
