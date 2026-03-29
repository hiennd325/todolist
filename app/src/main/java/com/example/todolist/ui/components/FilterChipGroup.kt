package com.example.todolist.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = filterMode == FilterMode.ALL,
            onClick = { onFilterModeChanged(FilterMode.ALL) },
            label = { Text("Tất cả") },
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
                } else null
            )
        }

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
                } else null
            )
        }

        FilterChip(
            selected = !showCompleted,
            onClick = { onShowCompletedChanged(!showCompleted) },
            label = { Text("Ẩn hoàn thành") },
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
