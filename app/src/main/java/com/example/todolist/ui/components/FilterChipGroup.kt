package com.example.todolist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
 import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
 import androidx.compose.material.icons.Icons
 import androidx.compose.material.icons.filled.Check
 import androidx.compose.material.icons.filled.Star
 import androidx.compose.material.icons.filled.Today
 import androidx.compose.material.icons.filled.Upcoming
 import androidx.compose.material.icons.filled.Warning
 import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ... existing chips ...
        // All
        FilterChip(
            selected = filterMode == FilterMode.ALL,
            onClick = { onFilterModeChanged(FilterMode.ALL) },
            label = { Text("All") },
            leadingIcon = if (filterMode == FilterMode.ALL) {
                {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        // Starred
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
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (filterMode == FilterMode.STARRED) 
                    Color(0xFFFFD700).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                selectedLabelColor = if (filterMode == FilterMode.STARRED) 
                    Color(0xFFFFA500) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Today
        FilterChip(
            selected = filterMode == FilterMode.TODAY,
            onClick = {
                if (filterMode == FilterMode.TODAY) {
                    onFilterModeChanged(FilterMode.ALL)
                } else {
                    onFilterModeChanged(FilterMode.TODAY)
                }
            },
            label = { Text("Today") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        // Upcoming
        FilterChip(
            selected = filterMode == FilterMode.UPCOMING,
            onClick = {
                if (filterMode == FilterMode.UPCOMING) {
                    onFilterModeChanged(FilterMode.ALL)
                } else {
                    onFilterModeChanged(FilterMode.UPCOMING)
                }
            },
            label = { Text("Upcoming") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Upcoming,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        // Overdue
        FilterChip(
            selected = filterMode == FilterMode.OVERDUE,
            onClick = {
                if (filterMode == FilterMode.OVERDUE) {
                    onFilterModeChanged(FilterMode.ALL)
                } else {
                    onFilterModeChanged(FilterMode.OVERDUE)
                }
            },
            label = { Text("Overdue") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
            )
        )

        // Completed Today
        FilterChip(
            selected = filterMode == FilterMode.COMPLETED_TODAY,
            onClick = {
                if (filterMode == FilterMode.COMPLETED_TODAY) {
                    onFilterModeChanged(FilterMode.ALL)
                } else {
                    onFilterModeChanged(FilterMode.COMPLETED_TODAY)
                }
            },
            label = { Text("Done Today") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        // Show/Hide Completed
        FilterChip(
            selected = !showCompleted,
            onClick = { onShowCompletedChanged(!showCompleted) },
            label = { Text(if (!showCompleted) "Show completed" else "Hide completed") },
            leadingIcon = {
                Icon(
                    imageVector = if (!showCompleted) Icons.Default.CheckCircle else Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}
