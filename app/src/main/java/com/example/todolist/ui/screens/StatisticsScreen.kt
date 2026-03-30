package com.example.todolist.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.data.model.CategoryStatistic
import com.example.todolist.data.model.DailyStatistic
import com.example.todolist.data.model.PriorityStatistic
import com.example.todolist.data.model.color
import com.example.todolist.ui.viewmodel.StatisticsUiState
import com.example.todolist.ui.viewmodel.StatisticsViewModel
import com.example.todolist.ui.viewmodel.TimeRange
import kotlin.math.max

private val CategoryColors = listOf(
    Color(0xFF2196F3), // Work - Blue
    Color(0xFF9C27B0), // Personal - Purple
    Color(0xFFFF9800), // Shopping - Orange
    Color(0xFFF44336), // Health - Red
    Color(0xFF607D8B)  // Other - Grey
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Statistics",
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TimeRangeSelector(
                        selectedRange = selectedTimeRange,
                        onRangeSelected = viewModel::setTimeRange
                    )
                }

                item {
                    OverviewCards(
                        totalCount = uiState.totalCount,
                        completedCount = uiState.completedCount,
                        pendingCount = uiState.pendingCount,
                        starredCount = uiState.starredCount
                    )
                }

                item {
                    CompletionRateCard(
                        completionRate = uiState.completionRate,
                        completedCount = uiState.completedCount,
                        totalCount = uiState.totalCount
                    )
                }

                if (uiState.categoryStats.isNotEmpty()) {
                    item {
                        CategoryPieChartCard(categoryStats = uiState.categoryStats)
                    }
                }

                if (uiState.priorityStats.isNotEmpty()) {
                    item {
                        PriorityBarChartCard(priorityStats = uiState.priorityStats)
                    }
                }

                if (uiState.dailyStats.isNotEmpty()) {
                    item {
                        DailyTrendLineChartCard(dailyStats = uiState.dailyStats)
                    }
                }

                if (uiState.weeklyStats.isNotEmpty()) {
                    item {
                        WeeklyTrendChartCard(weeklyStats = uiState.weeklyStats)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeRangeSelector(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeRange.entries.forEach { range ->
            val isSelected = range == selectedRange
            FilterChip(
                selected = isSelected,
                onClick = { onRangeSelected(range) },
                label = { Text(range.label) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun OverviewCards(
    totalCount: Int,
    completedCount: Int,
    pendingCount: Int,
    starredCount: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Total",
                value = totalCount.toString(),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Completed",
                value = completedCount.toString(),
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                label = "Pending",
                value = pendingCount.toString(),
                color = Color(0xFFFF9800),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Starred",
                value = starredCount.toString(),
                color = Color(0xFFFFD700),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompletionRateCard(
    completionRate: Float,
    completedCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Completion Rate",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { completionRate },
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${(completionRate * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$completedCount of $totalCount tasks completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryPieChartCard(categoryStats: List<CategoryStatistic>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Category Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val totalItems = categoryStats.sumOf { it.total }

            if (totalItems > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(160.dp)
                            .padding(8.dp)
                    ) {
                        val centerX = size.width / 2
                        val centerY = size.height / 2
                        val radius = minOf(centerX, centerY) * 0.9f

                        var startAngle = -90f

                        categoryStats.forEachIndexed { index, stat ->
                            val sweepAngle = (stat.total.toFloat() / totalItems.toFloat()) * 360f
                            val color = CategoryColors.getOrElse(index) { CategoryColors.last() }

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(centerX - radius, centerY - radius),
                                size = Size(radius * 2, radius * 2)
                            )

                            startAngle += sweepAngle
                        }

                        // Inner circle for donut effect
                        drawCircle(
                            color = Color.White,
                            radius = radius * 0.55f,
                            center = Offset(centerX, centerY)
                        )

                        // Center text
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                color = android.graphics.Color.BLACK
                                textSize = 36f
                                isFakeBoldText = true
                            }
                            drawText(
                                totalItems.toString(),
                                centerX,
                                centerY - 8,
                                paint
                            )
                            paint.textSize = 20f
                            paint.isFakeBoldText = false
                            paint.color = android.graphics.Color.GRAY
                            drawText(
                                "Total",
                                centerX,
                                centerY + 20,
                                paint
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categoryStats.forEachIndexed { index, stat ->
                            val color = CategoryColors.getOrElse(index) { CategoryColors.last() }
                            val percentage = if (totalItems > 0) {
                                (stat.total.toFloat() / totalItems.toFloat() * 100).toInt()
                            } else 0

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stat.category.label,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${stat.total} tasks ($percentage%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityBarChartCard(priorityStats: List<PriorityStatistic>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Priority Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val maxTotal = priorityStats.maxOfOrNull { it.total } ?: 0

            if (maxTotal > 0) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val barWidth = size.width / (priorityStats.size * 2.5f)
                    val spacing = barWidth * 0.5f
                    val chartHeight = size.height - 40.dp.toPx()
                    val startX = barWidth * 0.5f

                    priorityStats.forEachIndexed { index, stat ->
                        val x = startX + index * (barWidth * 2 + spacing)
                        val totalHeight = (stat.total.toFloat() / maxTotal.toFloat()) * chartHeight
                        val completedHeight = (stat.completed.toFloat() / maxTotal.toFloat()) * chartHeight
                        val priority = com.example.todolist.data.model.Priority.fromString(stat.priority.name)
                        val color = priority.color

                        // Total bar (background)
                        drawRoundRect(
                            color = color.copy(alpha = 0.3f),
                            topLeft = Offset(x, chartHeight - totalHeight),
                            size = Size(barWidth, totalHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )

                        // Completed bar (overlay)
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(x, chartHeight - completedHeight),
                            size = Size(barWidth, completedHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )

                        // Label
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint()
                            paint.textAlign = android.graphics.Paint.Align.CENTER
                            paint.color = android.graphics.Color.GRAY
                            paint.textSize = 24f
                            drawText(
                                stat.priority.label,
                                x + barWidth / 2,
                                chartHeight + 30f,
                                paint
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    priorityStats.forEach { stat ->
                        val priority = com.example.todolist.data.model.Priority.fromString(stat.priority.name)
                        val color = priority.color

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stat.priority.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${stat.completed}/${stat.total}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyTrendLineChartCard(dailyStats: List<DailyStatistic>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Daily Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = Color(0xFF2196F3), label = "Created")
                LegendItem(color = Color(0xFF4CAF50), label = "Completed")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (dailyStats.isNotEmpty()) {
                val maxCreated = dailyStats.maxOfOrNull { it.created } ?: 1
                val maxCompleted = dailyStats.maxOfOrNull { it.completed } ?: 1
                val maxValue = max(max(maxCreated, maxCompleted), 1)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val padding = 16.dp.toPx()
                    val chartWidth = size.width - padding * 2
                    val chartHeight = size.height - padding * 2
                    val stepX = if (dailyStats.size > 1) chartWidth / (dailyStats.size - 1) else chartWidth

                    // Grid lines
                    for (i in 0..4) {
                        val y = padding + chartHeight * i / 4
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(padding, y),
                            end = Offset(size.width - padding, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Created line
                    val createdPath = Path()
                    val createdPoints = dailyStats.mapIndexed { index, stat ->
                        val x = padding + index * stepX
                        val y = padding + chartHeight - (stat.created.toFloat() / maxValue) * chartHeight
                        Offset(x, y)
                    }

                    if (createdPoints.isNotEmpty()) {
                        createdPath.moveTo(createdPoints.first().x, createdPoints.first().y)
                        createdPoints.drop(1).forEach { point ->
                            createdPath.lineTo(point.x, point.y)
                        }

                        drawPath(
                            path = createdPath,
                            color = Color(0xFF2196F3),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        // Data points
                        createdPoints.forEach { point ->
                            drawCircle(
                                color = Color(0xFF2196F3),
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                    }

                    // Completed line
                    val completedPath = Path()
                    val completedPoints = dailyStats.mapIndexed { index, stat ->
                        val x = padding + index * stepX
                        val y = padding + chartHeight - (stat.completed.toFloat() / maxValue) * chartHeight
                        Offset(x, y)
                    }

                    if (completedPoints.isNotEmpty()) {
                        completedPath.moveTo(completedPoints.first().x, completedPoints.first().y)
                        completedPoints.drop(1).forEach { point ->
                            completedPath.lineTo(point.x, point.y)
                        }

                        drawPath(
                            path = completedPath,
                            color = Color(0xFF4CAF50),
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        completedPoints.forEach { point ->
                            drawCircle(
                                color = Color(0xFF4CAF50),
                                radius = 4.dp.toPx(),
                                center = point
                            )
                        }
                    }
                }

                // Date labels
                if (dailyStats.size <= 7) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        dailyStats.forEach { stat ->
                            Text(
                                text = stat.date.substring(5), // MM-dd
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dailyStats.first().date.substring(5),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dailyStats[dailyStats.size / 2].date.substring(5),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dailyStats.last().date.substring(5),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyTrendChartCard(weeklyStats: List<com.example.todolist.data.model.WeeklyStatistic>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Weekly Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LegendItem(color = Color(0xFF2196F3), label = "Created")
                LegendItem(color = Color(0xFF4CAF50), label = "Completed")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (weeklyStats.isNotEmpty()) {
                val maxCreated = weeklyStats.maxOfOrNull { it.created } ?: 1
                val maxCompleted = weeklyStats.maxOfOrNull { it.completed } ?: 1
                val maxValue = max(max(maxCreated, maxCompleted), 1)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val padding = 16.dp.toPx()
                    val chartWidth = size.width - padding * 2
                    val chartHeight = size.height - padding * 2
                    val barGroupWidth = chartWidth / weeklyStats.size
                    val barWidth = barGroupWidth * 0.35f
                    val barGap = barGroupWidth * 0.1f

                    // Grid lines
                    for (i in 0..4) {
                        val y = padding + chartHeight * i / 4
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(padding, y),
                            end = Offset(size.width - padding, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    weeklyStats.forEachIndexed { index, stat ->
                        val groupX = padding + index * barGroupWidth + barGroupWidth * 0.15f

                        val createdHeight = (stat.created.toFloat() / maxValue) * chartHeight
                        val completedHeight = (stat.completed.toFloat() / maxValue) * chartHeight

                        // Created bar
                        drawRoundRect(
                            color = Color(0xFF2196F3),
                            topLeft = Offset(
                                groupX,
                                padding + chartHeight - createdHeight
                            ),
                            size = Size(barWidth, createdHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )

                        // Completed bar
                        drawRoundRect(
                            color = Color(0xFF4CAF50),
                            topLeft = Offset(
                                groupX + barWidth + barGap,
                                padding + chartHeight - completedHeight
                            ),
                            size = Size(barWidth, completedHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                }

                // Week labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyStats.forEach { stat ->
                        Text(
                            text = stat.weekStart.substring(5), // MM-dd
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
