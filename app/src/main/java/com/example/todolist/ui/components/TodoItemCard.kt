package com.example.todolist.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.model.color
 import java.text.SimpleDateFormat
 import java.util.Date
 import java.util.Locale
 import java.util.concurrent.TimeUnit
 
 private fun formatDuration(minutes: Int): String {
     return when {
         minutes < 60 -> "$minutes min"
         minutes % 60 == 0 -> "${minutes / 60}h"
         else -> "${minutes / 60}h ${minutes % 60}m"
     }
 }
 
 @Composable
fun TodoItemCard(
    todo: TodoItem,
    onToggleComplete: () -> Unit,
    onToggleStarred: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "cardColor"
    )

    val contentAlpha = if (todo.isCompleted) 0.5f else 1f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onEdit),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(todo.priority.color)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(contentAlpha)
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox
                Checkbox(
                    checked = todo.isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    modifier = Modifier.size(24.dp),
                    colors = CheckboxDefaults.colors()
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    // Title
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Description
                    if (todo.description.isNotEmpty()) {
                        Text(
                            text = todo.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Tags row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = todo.category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = todo.category.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Recurrence indicator
                        if (todo.recurrenceType != RecurrenceType.NONE) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Text(
                                    text = todo.recurrenceType.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }

                        // Deadline
                        todo.deadline?.let { deadline ->
                            val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
                            val now = System.currentTimeMillis()
                            val daysUntil = TimeUnit.MILLISECONDS.toDays(deadline - now)

                            val deadlineText = when {
                                daysUntil < 0 -> "Overdue ${-daysUntil} days"
                                daysUntil == 0L -> "Today ${SimpleDateFormat("HH:mm", Locale.US).format(Date(deadline))}"
                                daysUntil == 1L -> "Tomorrow ${SimpleDateFormat("HH:mm", Locale.US).format(Date(deadline))}"
                                else -> dateFormat.format(Date(deadline))
                            }

                            val deadlineColor = when {
                                daysUntil < 0 -> MaterialTheme.colorScheme.error
                                daysUntil <= 2 -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = deadlineColor
                                )
                                Text(
                                    text = deadlineText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = deadlineColor
                                )
                         }
                     }
 
                     // Estimated Duration
                     if (todo.estimatedDurationMinutes != null && todo.estimatedDurationMinutes > 0) {
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                             horizontalArrangement = Arrangement.spacedBy(2.dp)
                         ) {
                             Icon(
                                 imageVector = Icons.Default.DateRange,
                                 contentDescription = null,
                                 modifier = Modifier.size(12.dp),
                                 tint = MaterialTheme.colorScheme.secondary
                             )
                             Text(
                                 text = formatDuration(todo.estimatedDurationMinutes),
                                 style = MaterialTheme.typography.labelSmall,
                                 color = MaterialTheme.colorScheme.secondary
                             )
                         }
                     }
 
                     // Reminder indicator
                        todo.reminderTime?.let { reminderTime ->
                            val reminderColor = if (reminderTime < System.currentTimeMillis()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            }
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(reminderColor, shape = RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }

                // Star button
                IconButton(
                    onClick = onToggleStarred,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (todo.isStarred) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (todo.isStarred) "Unstar" else "Star",
                        tint = if (todo.isStarred) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Delete button
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
