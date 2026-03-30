package com.example.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.TaskList

@Composable
fun TaskListDrawer(
    taskLists: List<TaskList>,
    currentTaskListId: Int,
    starredCount: Int,
    onTaskListSelected: (Int) -> Unit,
    onStarredClick: () -> Unit,
    onAddTaskList: (String, String) -> Unit,
    onUpdateTaskList: (TaskList) -> Unit,
    onDeleteTaskList: (TaskList) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTaskList by remember { mutableStateOf<TaskList?>(null) }
    var deletingTaskList by remember { mutableStateOf<TaskList?>(null) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Task Lists",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        // Starred item
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700)
                )
            },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Starred")
                    if (starredCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = starredCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            },
            selected = false,
            onClick = onStarredClick,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Task Lists
        Text(
            text = "Lists",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(taskLists) { taskList ->
                val isSelected = taskList.id == currentTaskListId
                NavigationDrawerItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(taskList.color))
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                        )
                    },
                    label = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = taskList.name,
                                modifier = Modifier.weight(1f)
                            )
                            if (isSelected) {
                                IconButton(
                                    onClick = { editingTaskList = taskList },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    selected = isSelected,
                    onClick = { onTaskListSelected(taskList.id) },
                    modifier = Modifier.padding(horizontal = 12.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        // Add new list button
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            label = { Text("Create new list") },
            selected = false,
            onClick = { showAddDialog = true },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }

    // Add/Edit Dialog
    if (showAddDialog) {
        TaskListDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                onAddTaskList(name, color)
                showAddDialog = false
            }
        )
    }

    editingTaskList?.let { taskList ->
        TaskListDialog(
            taskList = taskList,
            onDismiss = { editingTaskList = null },
            onConfirm = { name, color ->
                onUpdateTaskList(taskList.copy(name = name, color = color))
                editingTaskList = null
            },
            onDelete = {
                onDeleteTaskList(taskList)
                editingTaskList = null
            }
        )
    }
}

@Composable
fun TaskListDialog(
    taskList: TaskList? = null,
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf(taskList?.name ?: "") }
    var selectedColor by remember { mutableStateOf(taskList?.color ?: "#00897B") }
    var showError by remember { mutableStateOf(false) }

    val colors = listOf(
        "#00897B", "#2196F3", "#4CAF50", "#FF9800",
        "#F44336", "#9C27B0", "#607D8B", "#795548"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (taskList == null) "Create New List" else "Edit List")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (showError && it.isNotBlank()) showError = false
                    },
                    label = { Text("List Name") },
                    isError = showError && name.isBlank(),
                    supportingText = if (showError && name.isBlank()) {
                        { Text("Name cannot be empty") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(color))
                                    } catch (e: Exception) {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                                .clickable { selectedColor = color }
                                .then(
                                    if (color == selectedColor) {
                                        Modifier.padding(2.dp)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (color == selectedColor) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(name.trim(), selectedColor)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null && taskList != null) {
                    TextButton(onClick = onDelete) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
