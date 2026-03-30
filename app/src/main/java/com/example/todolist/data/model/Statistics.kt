package com.example.todolist.data.model

data class CategoryStatistic(
    val category: Category,
    val total: Int,
    val completed: Int
)

data class PriorityStatistic(
    val priority: Priority,
    val total: Int,
    val completed: Int
)

data class DailyStatistic(
    val date: String, // Format: "yyyy-MM-dd"
    val created: Int,
    val completed: Int
)

data class WeeklyStatistic(
    val weekStart: String,
    val weekEnd: String,
    val created: Int,
    val completed: Int
)

data class OverallStatistics(
    val totalCount: Int,
    val completedCount: Int,
    val starredCount: Int,
    val completionRate: Float,
    val categoryStats: List<CategoryStatistic>,
    val priorityStats: List<PriorityStatistic>,
    val dailyStats: List<DailyStatistic>,
    val weeklyStats: List<WeeklyStatistic>
)
