package com.example.todolist.data.model

data class CategoryStatisticResult(
    val category: String,
    val total: Int,
    val completed: Int
)

data class PriorityStatisticResult(
    val priority: String,
    val total: Int,
    val completed: Int
)

data class DailyStatisticResult(
    val date: String,
    val created: Int,
    val completed: Int
)

data class WeeklyStatisticResult(
    val week: String,
    val created: Int,
    val completed: Int
)
