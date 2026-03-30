package com.example.todolist.data.repository

import com.example.todolist.data.local.TodoDao
import com.example.todolist.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class StatisticsRepository(private val todoDao: TodoDao) {

    val totalCount: Flow<Int> = todoDao.getTodoCount()
    val completedCount: Flow<Int> = todoDao.getCompletedCount()
    val starredCount: Flow<Int> = todoDao.getStarredCount()

    fun getCompletionRate(): Flow<Float> {
        return totalCount.combine(completedCount) { total, completed ->
            if (total == 0) 0f
            else completed.toFloat() / total.toFloat()
        }
    }

    fun getStatisticsByCategory(): Flow<List<CategoryStatistic>> {
        return todoDao.getStatisticsByCategory().map { list ->
            list.map { result ->
                CategoryStatistic(
                    category = Category.fromString(result.category),
                    total = result.total,
                    completed = result.completed
                )
            }
        }
    }

    fun getStatisticsByPriority(): Flow<List<PriorityStatistic>> {
        return todoDao.getStatisticsByPriority().map { list ->
            list.map { result ->
                PriorityStatistic(
                    priority = Priority.fromString(result.priority),
                    total = result.total,
                    completed = result.completed
                )
            }
        }
    }

    fun getDailyStatistics(days: Int = 30): Flow<List<DailyStatistic>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val startDate = calendar.timeInMillis

        return todoDao.getDailyStatistics(startDate).map { list ->
            list.map { result ->
                DailyStatistic(
                    date = result.date,
                    created = result.created,
                    completed = result.completed
                )
            }
        }
    }

    fun getWeeklyStatistics(weeks: Int = 12): Flow<List<WeeklyStatistic>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -weeks)
        val startDate = calendar.timeInMillis

        return todoDao.getWeeklyStatistics(startDate).map { list ->
            list.map { result ->
                WeeklyStatistic(
                    weekStart = result.week,
                    weekEnd = result.week,
                    created = result.created,
                    completed = result.completed
                )
            }
        }
    }

    fun getOverallStatistics(): Flow<OverallStatistics> {
        return combine(
            totalCount,
            completedCount,
            starredCount,
            getStatisticsByCategory(),
            getStatisticsByPriority(),
            getDailyStatistics(),
            getWeeklyStatistics()
        ) { args ->
            val total = args[0] as Int
            val completed = args[1] as Int
            val starred = args[2] as Int
            val categoryStats = args[3] as List<CategoryStatistic>
            val priorityStats = args[4] as List<PriorityStatistic>
            val dailyStats = args[5] as List<DailyStatistic>
            val weeklyStats = args[6] as List<WeeklyStatistic>

            OverallStatistics(
                totalCount = total,
                completedCount = completed,
                starredCount = starred,
                completionRate = if (total == 0) 0f else completed.toFloat() / total.toFloat(),
                categoryStats = categoryStats,
                priorityStats = priorityStats,
                dailyStats = dailyStats,
                weeklyStats = weeklyStats
            )
        }
    }
}
