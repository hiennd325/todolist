package com.example.todolist.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.TodoItem
import com.example.todolist.ui.theme.TodolistTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTodoDialog(
    todo: TodoItem? = null,
    taskListId: Int = 1,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        priority: Priority,
        category: Category,
        deadline: Long?,
        reminderTime: Long?,
        recurrenceType: RecurrenceType,
        isStarred: Boolean,
        taskListId: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(todo?.category ?: Category.OTHER) }
    var deadline by remember { mutableLongStateOf(todo?.deadline ?: 0L) }
    var hasDeadline by remember { mutableStateOf(todo?.deadline != null) }
    var reminderTime by remember { mutableLongStateOf(todo?.reminderTime ?: 0L) }
    var hasReminder by remember { mutableStateOf(todo?.reminderTime != null) }
    var selectedRecurrence by remember { mutableStateOf(todo?.recurrenceType ?: RecurrenceType.NONE) }
    var isStarred by remember { mutableStateOf(todo?.isStarred ?: false) }
    var showError by remember { mutableStateOf(false) }
    var triggerShake by remember { mutableStateOf(0) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    val titleError = title.isBlank()

    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(triggerShake) {
        if (triggerShake > 0) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -12f at 50
                    12f at 100
                    -10f at 150
                    10f at 200
                    -6f at 250
                    6f at 300
                    -3f at 350
                    0f at 400
                }
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (todo == null) Icons.Default.Add else Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (todo == null) "Add New Task" else "Edit Task")
                Spacer(modifier = Modifier.weight(1f))
                // Star button
                IconButton(onClick = { isStarred = !isStarred }) {
                    Icon(
                        imageVector = if (isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (isStarred) "Unstar" else "Star",
                        tint = if (isStarred) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (showError && it.isNotBlank()) showError = false
                    },
                    label = { Text("Title *") },
                    isError = showError && titleError,
                    supportingText = if (showError && titleError) {
                        { Text("Title cannot be empty") }
                    } else null,
                    colors = if (showError && titleError) {
                        OutlinedTextFieldDefaults.colors(
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorSupportingTextColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        OutlinedTextFieldDefaults.colors()
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationX = shakeOffset.value
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                PriorityDropdown(
                    selectedPriority = selectedPriority,
                    onPrioritySelected = { selectedPriority = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoryDropdown(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Recurrence dropdown
                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedRecurrence.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Recurrence") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        RecurrenceType.entries.forEach { recurrence ->
                            DropdownMenuItem(
                                text = { Text(recurrence.label) },
                                onClick = {
                                    selectedRecurrence = recurrence
                                    recurrenceExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                // Deadline section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hasDeadline) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = dateFormat.format(Date(deadline)),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Date") },
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = {
                                            val calendar = Calendar.getInstance()
                                            calendar.timeInMillis = deadline
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    val cal = Calendar.getInstance()
                                                    cal.timeInMillis = deadline
                                                    cal.set(year, month, dayOfMonth)
                                                    deadline = cal.timeInMillis
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.DateRange,
                                                contentDescription = "Pick date"
                                            )
                                        }
                                        IconButton(onClick = {
                                            hasDeadline = false
                                            deadline = 0L
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Remove deadline"
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = timeFormat.format(Date(deadline)),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Time") },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = deadline
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                val cal = Calendar.getInstance()
                                                cal.timeInMillis = deadline
                                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                cal.set(Calendar.MINUTE, minute)
                                                deadline = cal.timeInMillis
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Pick time"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        TextButton(
                            onClick = {
                                hasDeadline = true
                                deadline = System.currentTimeMillis()
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val cal = Calendar.getInstance()
                                        cal.set(year, month, dayOfMonth, 23, 59, 59)
                                        deadline = cal.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Deadline")
                        }
                    }
                }

                // Reminder section
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hasReminder) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = dateFormat.format(Date(reminderTime)),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reminder Date") },
                                trailingIcon = {
                                    Row {
                                        IconButton(onClick = {
                                            val calendar = Calendar.getInstance()
                                            calendar.timeInMillis = reminderTime
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    val cal = Calendar.getInstance()
                                                    cal.timeInMillis = reminderTime
                                                    cal.set(year, month, dayOfMonth)
                                                    reminderTime = cal.timeInMillis
                                                },
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Alarm,
                                                contentDescription = "Pick reminder date"
                                            )
                                        }
                                        IconButton(onClick = {
                                            hasReminder = false
                                            reminderTime = 0L
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Clear,
                                                contentDescription = "Remove reminder"
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = timeFormat.format(Date(reminderTime)),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reminder Time") },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = reminderTime
                                        TimePickerDialog(
                                            context,
                                            { _, hourOfDay, minute ->
                                                val cal = Calendar.getInstance()
                                                cal.timeInMillis = reminderTime
                                                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                                cal.set(Calendar.MINUTE, minute)
                                                reminderTime = cal.timeInMillis
                                            },
                                            calendar.get(Calendar.HOUR_OF_DAY),
                                            calendar.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Alarm,
                                            contentDescription = "Pick reminder time"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        TextButton(
                            onClick = {
                                hasReminder = true
                                reminderTime = System.currentTimeMillis()
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val cal = Calendar.getInstance()
                                        cal.set(year, month, dayOfMonth, 9, 0, 0)
                                        reminderTime = cal.timeInMillis
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Reminder")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleError) {
                        showError = true
                        triggerShake++
                    } else {
                        onConfirm(
                            title.trim(),
                            description.trim(),
                            selectedPriority,
                            selectedCategory,
                            if (hasDeadline) deadline else null,
                            if (hasReminder) reminderTime else null,
                            selectedRecurrence,
                            isStarred,
                            taskListId
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddEditTodoDialogAddPreview() {
    TodolistTheme {
        AddEditTodoDialog(
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditTodoDialogEditPreview() {
    TodolistTheme {
        AddEditTodoDialog(
            todo = TodoItem(
                title = "Sample Task",
                description = "This is a sample task description",
                priority = Priority.HIGH,
                category = Category.WORK,
                isStarred = true
            ),
            onDismiss = {},
            onConfirm = { _, _, _, _, _, _, _, _, _ -> }
        )
    }
}
