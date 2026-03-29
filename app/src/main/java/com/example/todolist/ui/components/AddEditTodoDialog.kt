package com.example.todolist.ui.components

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.TodoItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun AddEditTodoDialog(
    todo: TodoItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, priority: Priority, category: Category, deadline: Long?) -> Unit
) {
    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(todo?.priority ?: Priority.MEDIUM) }
    var selectedCategory by remember { mutableStateOf(todo?.category ?: Category.OTHER) }
    var deadline by remember { mutableLongStateOf(todo?.deadline ?: 0L) }
    var hasDeadline by remember { mutableStateOf(todo?.deadline != null) }
    var showError by remember { mutableStateOf(false) }
    var triggerShake by remember { mutableStateOf(0) }

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))

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
                Text(text = if (todo == null) "Thêm công việc mới" else "Chỉnh sửa công việc")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (showError && it.isNotBlank()) showError = false
                    },
                    label = { Text("Tiêu đề *") },
                    isError = showError && titleError,
                    supportingText = if (showError && titleError) {
                        { Text("Tiêu đề không được để trống") }
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
                    label = { Text("Mô tả") },
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

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (hasDeadline) {
                        OutlinedTextField(
                            value = dateFormat.format(Date(deadline)),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hạn chót") },
                            trailingIcon = {
                                Row {
                                    IconButton(onClick = {
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = deadline
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
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = "Chọn ngày"
                                        )
                                    }
                                    IconButton(onClick = {
                                        hasDeadline = false
                                        deadline = 0L
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Xóa hạn chót"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
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
                            Text("Thêm hạn chót")
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
                            if (hasDeadline) deadline else null
                        )
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
