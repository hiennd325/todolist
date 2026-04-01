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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
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
      val hours = minutes / 60
      val mins = minutes % 60
      return when {
          hours > 0 && mins > 0 -> "${hours}h ${mins}m"
          hours > 0 -> "${hours}h"
          else -> "${mins}m"
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
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // Accessibility: content description is provided through semantics
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = todo.priority.color,
                        size = androidx.compose.ui.geometry.Size(4.dp.toPx(), this.size.height)
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(4.dp))

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

                    Spacer(modifier = Modifier.height(4.dp))

                    // Category & Recurrence row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Category badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = todo.category.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = todo.category.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // Recurrence
                        if (todo.recurrenceType != RecurrenceType.NONE) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Repeat,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = todo.recurrenceType.label,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        // Overdue indicator (if task is overdue)
                        if (todo.deadline != null && todo.deadline < System.currentTimeMillis() && !todo.isCompleted) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "Overdue",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    // Deadline & Duration row
                    if (todo.deadline != null || (todo.estimatedDurationMinutes != null && todo.estimatedDurationMinutes > 0)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Deadline
                            todo.deadline?.let { deadline ->
                                val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US)
                                val now = System.currentTimeMillis()
                                val daysUntil = TimeUnit.MILLISECONDS.toDays(deadline - now)

                                val deadlineText = when {
                                    daysUntil < 0 -> "${-daysUntil} days overdue"
                                    daysUntil == 0L -> "Today"
                                    daysUntil == 1L -> "Tomorrow"
                                    daysUntil <= 7 -> "In ${daysUntil} days"
                                    else -> dateFormat.format(Date(deadline))
                                }

                                val isUrgent = daysUntil <= 2 && daysUntil >= 0

                                Row(
                                    modifier = Modifier.weight(1f, fill = false),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (daysUntil < 0) MaterialTheme.colorScheme.error 
                                               else if (isUrgent) MaterialTheme.colorScheme.error 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = deadlineText,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = if (daysUntil < 0) MaterialTheme.colorScheme.error 
                                               else if (isUrgent) MaterialTheme.colorScheme.error 
                                               else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    // Time for today/tomorrow
                                    if (daysUntil <= 1) {
                                        Text(
                                            text = SimpleDateFormat("HH:mm", Locale.US).format(Date(deadline)),
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Estimated Duration
                            if (todo.estimatedDurationMinutes != null && todo.estimatedDurationMinutes > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = formatDuration(todo.estimatedDurationMinutes),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }

                    // Reminder badge
                    if (todo.reminderTime != null) {
                        val isPastReminder = todo.reminderTime < System.currentTimeMillis()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPastReminder) MaterialTheme.colorScheme.errorContainer 
                                   else MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = if (isPastReminder) MaterialTheme.colorScheme.onErrorContainer 
                                         else MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (isPastReminder) MaterialTheme.colorScheme.error 
                                                   else MaterialTheme.colorScheme.tertiary,
                                            shape = RoundedCornerShape(3.dp)
                                        )
                                )
                                Text(
                                    text = if (isPastReminder) "Reminder passed" else "Reminder set",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
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
