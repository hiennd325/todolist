package com.example.todolist.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))

    val titleError = title.isBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (todo == null) "Thêm công việc mới" else "Chỉnh sửa công việc")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề *") },
                    isError = titleError && title.isNotEmpty(),
                    supportingText = if (titleError && title.isNotEmpty()) {
                        { Text("Tiêu đề không được để trống") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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

                Spacer(modifier = Modifier.height(8.dp))

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
            TextButton(
                onClick = {
                    if (!titleError) {
                        onConfirm(
                            title.trim(),
                            description.trim(),
                            selectedPriority,
                            selectedCategory,
                            if (hasDeadline) deadline else null
                        )
                    }
                },
                enabled = !titleError
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
