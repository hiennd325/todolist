package com.example.todolist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.TodoItem
import com.example.todolist.ui.components.AddEditTodoDialog
import com.example.todolist.ui.components.FilterChipGroup
import com.example.todolist.ui.components.TodoItemCard
import com.example.todolist.ui.viewmodel.SortMode
import com.example.todolist.ui.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterMode by viewModel.filterMode.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Danh sách công việc",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        if (uiState.totalCount > 0) {
                            Text(
                                text = "${uiState.completedCount}/${uiState.totalCount} hoàn thành",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm công việc"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = viewModel::setSearchQuery,
                        onSearch = { searchActive = false },
                        expanded = searchActive,
                        onExpandedChange = { searchActive = it },
                        placeholder = { Text("Tìm kiếm công việc...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Tìm kiếm"
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xóa"
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bộ lọc:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Sắp xếp"
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Theo thời gian tạo") },
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
                            text = { Text("Theo hạn chót") },
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
                            text = { Text("Theo độ ưu tiên") },
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
                    }
                }
            }

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

            if (uiState.totalCount > 0) {
                val progress = uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(6.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            if (uiState.todos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "Không tìm thấy công việc nào"
                            } else {
                                "Chưa có công việc nào"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nhấn nút + để thêm công việc mới",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
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
                            TodoItemCard(
                                todo = todo,
                                onToggleComplete = { viewModel.toggleComplete(todo) },
                                onEdit = { editingTodo = todo },
                                onDelete = { viewModel.deleteTodo(todo) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditTodoDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, description, priority, category, deadline ->
                viewModel.addTodo(title, description, priority, category, deadline)
                showAddDialog = false
            }
        )
    }

    editingTodo?.let { todo ->
        AddEditTodoDialog(
            todo = todo,
            onDismiss = { editingTodo = null },
            onConfirm = { title, description, priority, category, deadline ->
                viewModel.updateTodo(
                    todo.copy(
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        deadline = deadline
                    )
                )
                editingTodo = null
            }
        )
    }
}
