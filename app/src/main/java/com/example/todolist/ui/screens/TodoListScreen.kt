package com.example.todolist.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todolist.data.export.ExportImportManager
import com.example.todolist.data.model.TodoItem
import com.example.todolist.notification.NotificationScheduler
import com.example.todolist.ui.components.AddEditTodoDialog
import com.example.todolist.ui.components.FilterChipGroup
import com.example.todolist.ui.components.SubtaskSection
import com.example.todolist.ui.components.SwipeableTodoItemCard
import com.example.todolist.ui.components.TaskListDrawer
import com.example.todolist.ui.viewmodel.FilterMode
import com.example.todolist.ui.viewmodel.SortMode
import com.example.todolist.ui.viewmodel.TodoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    notificationScheduler: NotificationScheduler? = null,
    exportImportManager: ExportImportManager? = null,
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterMode by viewModel.filterMode.collectAsState()
    val subtasks by viewModel.subtasks.collectAsState()
    val taskLists by viewModel.taskLists.collectAsState()
    val currentTaskListId by viewModel.currentTaskListId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }
    var expandedTodoId by remember { mutableStateOf<Int?>(null) }
    var showMoveToListDialog by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val isSelectionMode = uiState.isSelectionMode
    val selectedTodoIds = uiState.selectedTodoIds

    // Export launcher
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = exportImportManager?.exportToJson(it) ?: false
                snackbarHostState.showSnackbar(
                    if (success) "Export successful!" else "Export failed"
                )
            }
        }
    }

    // Import launcher
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = exportImportManager?.importFromJson(it) ?: false
                snackbarHostState.showSnackbar(
                    if (success) "Import successful!" else "Import failed"
                )
            }
        }
    }

    // Get current task list name
    val currentTaskListName = taskLists.find { it.id == currentTaskListId }?.name ?: "My Tasks"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TaskListDrawer(
                taskLists = taskLists,
                currentTaskListId = currentTaskListId,
                starredCount = uiState.starredCount,
                onTaskListSelected = { taskListId ->
                    viewModel.setCurrentTaskList(taskListId)
                    scope.launch { drawerState.close() }
                },
                onStarredClick = {
                    viewModel.setFilterMode(
                        if (filterMode == com.example.todolist.ui.viewmodel.FilterMode.STARRED) {
                            com.example.todolist.ui.viewmodel.FilterMode.ALL
                        } else {
                            com.example.todolist.ui.viewmodel.FilterMode.STARRED
                        }
                    )
                    scope.launch { drawerState.close() }
                },
                onAddTaskList = { name, color ->
                    viewModel.addTaskList(name, color)
                },
                onUpdateTaskList = { taskList ->
                    viewModel.updateTaskList(taskList)
                },
                onDeleteTaskList = { taskList ->
                    viewModel.deleteTaskList(taskList)
                }
            )
        }
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.inverseSurface,
                        contentColor = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        if (isSelectionMode) {
                            Text(
                                text = "${selectedTodoIds.size} selected",
                                style = MaterialTheme.typography.titleLarge
                            )
                        } else {
                            Column {
                                Text(
                                    text = currentTaskListName,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                if (uiState.totalCount > 0) {
                                    Text(
                                        text = "${uiState.completedCount}/${uiState.totalCount} completed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isSelectionMode) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        },
                        titleContentColor = if (isSelectionMode) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    ),
                    navigationIcon = {
                        if (isSelectionMode) {
                            IconButton(onClick = { viewModel.toggleSelectionMode() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Exit selection mode"
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    },
                    actions = {
                        if (isSelectionMode) {
                            IconButton(onClick = {
                                if (selectedTodoIds.size == uiState.todos.size) {
                                    viewModel.clearSelection()
                                } else {
                                    viewModel.selectAllTodos()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.SelectAll,
                                    contentDescription = if (selectedTodoIds.size == uiState.todos.size) "Deselect all" else "Select all"
                                )
                            }
                        } else {
                            // Theme toggle
                            IconButton(onClick = onToggleTheme) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkTheme) "Switch to light mode" else "Switch to dark mode"
                                )
                            }
                            // Options menu
                            Box {
                                IconButton(onClick = { showOptionsMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More options"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showOptionsMenu,
                                    onDismissRequest = { showOptionsMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Settings") },
                                        onClick = {
                                            showOptionsMenu = false
                                            onNavigateToSettings()
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Export Data") },
                                        onClick = {
                                            showOptionsMenu = false
                                            exportLauncher.launch("todolist_backup_${System.currentTimeMillis()}.json")
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Upload,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Import Data") },
                                        onClick = {
                                            showOptionsMenu = false
                                            importLauncher.launch(arrayOf("application/json"))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Share Tasks") },
                                        onClick = {
                                            showOptionsMenu = false
                                            // Share current task list as text
                                            val tasks = uiState.todos.joinToString("\n") { todo ->
                                                val status = if (todo.isCompleted) "✓" else "○"
                                                "$status ${todo.title}"
                                            }
                                            val shareIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "My Tasks - $currentTaskListName\n\n$tasks")
                                                type = "text/plain"
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share tasks"))
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (isSelectionMode && selectedTodoIds.isNotEmpty()) {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.completeSelectedTodos() }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete All"
                                    )
                                    Text(
                                        text = "Complete",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.starSelectedTodos() }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Star All"
                                    )
                                    Text(
                                        text = "Star",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            IconButton(onClick = { showMoveToListDialog = true }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.MoveToInbox,
                                        contentDescription = "Move All"
                                    )
                                    Text(
                                        text = "Move",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.deleteSelectedTodos() }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete All",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Delete",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                if (!isSelectionMode) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add task"
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search bar
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = viewModel::setSearchQuery,
                            onSearch = { searchActive = false },
                            expanded = searchActive,
                            onExpandedChange = { searchActive = it },
                            placeholder = { Text("Search tasks...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    content = {}
                )

                // Filter and Sort row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (uiState.sortMode) {
                                    SortMode.CREATED_AT -> "Created At"
                                    SortMode.DEADLINE -> "Deadline"
                                    SortMode.PRIORITY -> "Priority"
                                    SortMode.MANUAL -> "Manual"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = "Sort"
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("By Created At") },
                                onClick = {
                                    viewModel.setSortMode(SortMode.CREATED_AT)
                                    showSortMenu = false
                                },
                                trailingIcon = {
                                    if (uiState.sortMode == SortMode.CREATED_AT) {
                                        Text("✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Deadline") },
                                onClick = {
                                    viewModel.setSortMode(SortMode.DEADLINE)
                                    showSortMenu = false
                                },
                                trailingIcon = {
                                    if (uiState.sortMode == SortMode.DEADLINE) {
                                        Text("✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("By Priority") },
                                onClick = {
                                    viewModel.setSortMode(SortMode.PRIORITY)
                                    showSortMenu = false
                                },
                                trailingIcon = {
                                    if (uiState.sortMode == SortMode.PRIORITY) {
                                        Text("✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Manual") },
                                onClick = {
                                    viewModel.setSortMode(SortMode.MANUAL)
                                    showSortMenu = false
                                },
                                trailingIcon = {
                                    if (uiState.sortMode == SortMode.MANUAL) {
                                        Text("✓", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                        }
                    }
                }

                // Filter chips
                FilterChipGroup(
                    filterMode = filterMode,
                    selectedCategory = uiState.selectedCategory,
                    selectedPriority = uiState.selectedPriority,
                    onFilterModeChanged = viewModel::setFilterMode,
                    onCategorySelected = viewModel::setSelectedCategory,
                    onPrioritySelected = viewModel::setSelectedPriority,
                    showCompleted = uiState.showCompleted,
                    onShowCompletedChanged = viewModel::setShowCompleted,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // Progress indicator
                if (uiState.totalCount > 0) {
                    val progress = uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Todo list
                if (uiState.todos.isEmpty()) {
                    EnhancedEmptyState(
                        searchQuery = searchQuery,
                        filterMode = filterMode,
                        onAddTask = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = if (isSelectionMode && selectedTodoIds.isNotEmpty()) 80.dp else 88.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.todos,
                            key = { it.id }
                        ) { todo ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(),
                                exit = fadeOut() + slideOutVertically()
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .combinedClickable(
                                                onClick = {
                                                    if (isSelectionMode) {
                                                        viewModel.toggleTodoSelection(todo.id)
                                                    } else {
                                                        expandedTodoId = if (expandedTodoId == todo.id) null else todo.id
                                                        editingTodo = todo
                                                    }
                                                },
                                                onLongClick = {
                                                    if (!isSelectionMode) {
                                                        viewModel.toggleSelectionMode()
                                                    }
                                                    viewModel.toggleTodoSelection(todo.id)
                                                }
                                            ),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isSelectionMode) {
                                            Checkbox(
                                                checked = todo.id in selectedTodoIds,
                                                onCheckedChange = { viewModel.toggleTodoSelection(todo.id) },
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                        SwipeableTodoItemCard(
                                            todo = todo,
                                            onToggleComplete = { viewModel.toggleComplete(todo) },
                                            onToggleStarred = { viewModel.toggleStarred(todo) },
                                            onEdit = {
                                                expandedTodoId = if (expandedTodoId == todo.id) null else todo.id
                                                editingTodo = todo
                                            },
                                            onDelete = { viewModel.deleteTodo(todo) },
                                            onShowUndoSnackbar = { undoAction ->
                                                scope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = "Task deleted",
                                                        actionLabel = "Undo"
                                                    )
                                                    if (result != androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                                        undoAction()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // Subtask section (expanded)
                                    if (expandedTodoId == todo.id) {
                                        val todoSubtasks = subtasks[todo.id] ?: emptyList()
                                        LaunchedEffect(todo.id) {
                                            viewModel.loadSubtasks(todo.id)
                                        }
                                        SubtaskSection(
                                            todoId = todo.id,
                                            subtasks = todoSubtasks,
                                            onAddSubtask = { title ->
                                                viewModel.addSubtask(todo.id, title)
                                            },
                                            onToggleSubtask = { subtask ->
                                                viewModel.toggleSubtask(subtask)
                                            },
                                            onDeleteSubtask = { subtask ->
                                                viewModel.deleteSubtask(subtask)
                                            },
                                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Move to list dialog (simple selection from taskLists)
    if (showMoveToListDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showMoveToListDialog = false },
            title = { Text("Move to List") },
            text = {
                Column {
                    taskLists.forEach { taskList ->
                        DropdownMenuItem(
                            text = { Text(taskList.name) },
                            onClick = {
                                viewModel.moveSelectedTodosToTaskList(taskList.id)
                                showMoveToListDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showMoveToListDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

     // Add dialog
     if (showAddDialog) {
         AddEditTodoDialog(
             taskListId = currentTaskListId,
             onDismiss = { showAddDialog = false },
             onConfirm = { title, description, priority, category, deadline, reminderTime, recurrenceType, estimatedDuration, isStarred, taskListId ->
                 viewModel.addTodo(
                     title = title,
                     description = description,
                     priority = priority,
                     category = category,
                     deadline = deadline,
                     reminderTime = reminderTime,
                     recurrenceType = recurrenceType,
                     estimatedDurationMinutes = estimatedDuration,
                     isStarred = isStarred,
                     taskListId = taskListId
                 )
                 showAddDialog = false
             }
         )
     }
 
     // Edit dialog
     editingTodo?.let { todo ->
         AddEditTodoDialog(
             todo = todo,
             taskListId = currentTaskListId,
             onDismiss = { editingTodo = null },
             onConfirm = { title, description, priority, category, deadline, reminderTime, recurrenceType, estimatedDuration, isStarred, taskListId ->
                 viewModel.updateTodo(
                     todo.copy(
                         title = title,
                         description = description,
                         priority = priority,
                         category = category,
                         deadline = deadline,
                         reminderTime = reminderTime,
                         recurrenceType = recurrenceType,
                         estimatedDurationMinutes = estimatedDuration,
                         isStarred = isStarred,
                         taskListId = taskListId
                     )
                 )
                 editingTodo = null
             }
         )
      }
}

@Composable
fun EnhancedEmptyState(
    searchQuery: String,
    filterMode: FilterMode,
    onAddTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = when {
                searchQuery.isNotEmpty() -> Icons.Default.Search
                filterMode == FilterMode.STARRED -> Icons.Default.Star
                else -> Icons.Default.AddCircle
            },
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = when {
                searchQuery.isNotEmpty() -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                filterMode == FilterMode.STARRED -> Color(0xFFFFD700).copy(alpha = 0.7f)
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when {
                searchQuery.isNotEmpty() -> "No tasks match your search"
                filterMode == FilterMode.STARRED -> "No starred tasks yet"
                filterMode == FilterMode.OVERDUE -> "No overdue tasks"
                filterMode == FilterMode.TODAY -> "No tasks for today"
                filterMode == FilterMode.UPCOMING -> "No upcoming tasks"
                filterMode == FilterMode.COMPLETED_TODAY -> "No tasks completed today"
                else -> "Your task list is empty"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                searchQuery.isNotEmpty() -> "Try a different search term or clear filters"
                filterMode != FilterMode.ALL && filterMode != FilterMode.STARRED -> "Switch to 'All' to see all tasks"
                else -> "Start by adding your first task"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddTask,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (searchQuery.isNotEmpty() || filterMode != FilterMode.ALL) "Add Task" else "Add Your First Task")
        }
    }
}
