package com.example.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.color
import com.example.todolist.ui.viewmodel.FilterMode

@Composable
fun FilterChipGroup(
    filterMode: FilterMode,
    selectedCategory: Category?,
    selectedPriority: Priority?,
    onFilterModeChanged: (FilterMode) -> Unit,
    onCategorySelected: (Category?) -> Unit,
    onPrioritySelected: (Priority?) -> Unit,
    showCompleted: Boolean,
    onShowCompletedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // All filter
            FilterChip(
                selected = filterMode == FilterMode.ALL,
                onClick = { onFilterModeChanged(FilterMode.ALL) },
                label = { Text("All") },
                leadingIcon = if (filterMode == FilterMode.ALL) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )

            // Starred filter
            FilterChip(
                selected = filterMode == FilterMode.STARRED,
                onClick = {
                    if (filterMode == FilterMode.STARRED) {
                        onFilterModeChanged(FilterMode.ALL)
                    } else {
                        onFilterModeChanged(FilterMode.STARRED)
                    }
                },
                label = { Text("Starred") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (filterMode == FilterMode.STARRED) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            Text(
                text = "Category:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Category.entries.forEach { category ->
                FilterChip(
                    selected = filterMode == FilterMode.BY_CATEGORY && selectedCategory == category,
                    onClick = {
                        if (filterMode == FilterMode.BY_CATEGORY && selectedCategory == category) {
                            onFilterModeChanged(FilterMode.ALL)
                            onCategorySelected(null)
                        } else {
                            onFilterModeChanged(FilterMode.BY_CATEGORY)
                            onCategorySelected(category)
                        }
                    },
                    label = { Text(category.label) },
                    leadingIcon = if (filterMode == FilterMode.BY_CATEGORY && selectedCategory == category) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
            }

            Text(
                text = "Priority:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Priority.entries.forEach { priority ->
                FilterChip(
                    selected = filterMode == FilterMode.BY_PRIORITY && selectedPriority == priority,
                    onClick = {
                        if (filterMode == FilterMode.BY_PRIORITY && selectedPriority == priority) {
                            onFilterModeChanged(FilterMode.ALL)
                            onPrioritySelected(null)
                        } else {
                            onFilterModeChanged(FilterMode.BY_PRIORITY)
                            onPrioritySelected(priority)
                        }
                    },
                    label = { Text(priority.label) },
                    leadingIcon = if (filterMode == FilterMode.BY_PRIORITY && selectedPriority == priority) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(priority.color)
                            )
                        }
                    }
                )
            }

            Text(
                text = "|",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                fontSize = 11.sp
            )

            FilterChip(
                selected = !showCompleted,
                onClick = { onShowCompletedChanged(!showCompleted) },
                label = { Text("Hide completed") },
                leadingIcon = if (!showCompleted) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else null
            )
        }
    }
}
