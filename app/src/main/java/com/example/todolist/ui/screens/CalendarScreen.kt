package com.example.todolist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.todolist.data.model.TodoItem
import com.example.todolist.ui.components.TodoItemCard
import com.example.todolist.ui.viewmodel.CalendarUiState
import com.example.todolist.ui.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val DayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val monthLabel = monthFormat.format(currentMonth.time)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calendar",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CalendarHeader(
                    monthLabel = monthLabel,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    onTodayClick = viewModel::goToToday
                )
            }

            item {
                CalendarGrid(
                    currentMonth = currentMonth,
                    todosByDate = uiState.todosByDate,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = viewModel::selectDate
                )
            }

            item {
                SelectedDateHeader(
                    selectedDate = uiState.selectedDate,
                    taskCount = uiState.selectedDateTodos.size
                )
            }

            if (uiState.selectedDateTodos.isEmpty()) {
                item {
                    EmptyDateCard()
                }
            } else {
                items(
                    items = uiState.selectedDateTodos,
                    key = { it.id }
                ) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggleComplete = {},
                        onToggleStarred = {},
                        onEdit = {},
                        onDelete = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarHeader(
    monthLabel: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Previous month"
            )
        }

        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onTodayClick) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today")
            }

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next month"
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Calendar,
    todosByDate: Map<String, List<TodoItem>>,
    selectedDate: String?,
    onDateSelected: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    val cal = currentMonth.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val mondayOffset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - Calendar.MONDAY

    val todayKey = dateFormat.format(Calendar.getInstance().time)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayNames.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalCells = mondayOffset + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNumber = cellIndex - mondayOffset + 1

                    if (dayNumber in 1..daysInMonth) {
                        cal.set(Calendar.DAY_OF_MONTH, dayNumber)
                        val dateKey = dateFormat.format(cal.time)
                        val isToday = dateKey == todayKey
                        val isSelected = dateKey == selectedDate
                        val hasTodos = todosByDate[dateKey]?.isNotEmpty() == true

                        CalendarDay(
                            day = dayNumber,
                            isToday = isToday,
                            isSelected = isSelected,
                            hasTodos = hasTodos,
                            modifier = Modifier.weight(1f),
                            onClick = { onDateSelected(dateKey) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasTodos: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isToday && !isSelected) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )

            if (hasTodos) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                )
            } else {
                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}

@Composable
private fun SelectedDateHeader(
    selectedDate: String?,
    taskCount: Int
) {
    if (selectedDate != null) {
        val displayFormat = remember { SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()) }
        val inputFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

        val displayDate = try {
            val date = inputFormat.parse(selectedDate)
            if (date != null) displayFormat.format(date) else selectedDate
        } catch (e: Exception) {
            selectedDate
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayDate,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (taskCount > 0) {
                Text(
                    text = "$taskCount task${if (taskCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyDateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No tasks for this date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
