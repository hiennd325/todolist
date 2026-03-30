package com.example.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.model.*
import com.example.todolist.data.repository.StatisticsRepository
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

data class StatisticsUiState(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val starredCount: Int = 0,
    val completionRate: Float = 0f,
    val categoryStats: List<CategoryStatistic> = emptyList(),
    val priorityStats: List<PriorityStatistic> = emptyList(),
    val dailyStats: List<DailyStatistic> = emptyList(),
    val weeklyStats: List<WeeklyStatistic> = emptyList(),
    val isLoading: Boolean = true,
    val selectedTimeRange: TimeRange = TimeRange.LAST_30_DAYS
)

enum class TimeRange(val label: String, val days: Int) {
    LAST_7_DAYS("7 days", 7),
    LAST_30_DAYS("30 days", 30),
    LAST_90_DAYS("90 days", 90),
    ALL_TIME("All time", -1)
}

class StatisticsViewModel(
    private val statisticsRepository: StatisticsRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _selectedTimeRange = MutableStateFlow(TimeRange.LAST_30_DAYS)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange

    val uiState: StateFlow<StatisticsUiState> = combine(
        statisticsRepository.totalCount,
        statisticsRepository.completedCount,
        statisticsRepository.starredCount,
        statisticsRepository.getStatisticsByCategory(),
        statisticsRepository.getStatisticsByPriority(),
        statisticsRepository.getDailyStatistics(),
        statisticsRepository.getWeeklyStatistics(),
        _selectedTimeRange
    ) { flows ->
        val totalCount = flows[0] as Int
        val completedCount = flows[1] as Int
        val starredCount = flows[2] as Int
        val categoryStats = flows[3] as List<CategoryStatistic>
        val priorityStats = flows[4] as List<PriorityStatistic>
        val dailyStats = flows[5] as List<DailyStatistic>
        val weeklyStats = flows[6] as List<WeeklyStatistic>
        val timeRange = flows[7] as TimeRange

        StatisticsUiState(
            totalCount = totalCount,
            completedCount = completedCount,
            pendingCount = totalCount - completedCount,
            starredCount = starredCount,
            completionRate = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f,
            categoryStats = categoryStats,
            priorityStats = priorityStats,
            dailyStats = filterDailyStatsByTimeRange(dailyStats, timeRange),
            weeklyStats = weeklyStats,
            isLoading = false,
            selectedTimeRange = timeRange
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    private fun filterDailyStatsByTimeRange(
        stats: List<DailyStatistic>,
        timeRange: TimeRange
    ): List<DailyStatistic> {
        if (timeRange == TimeRange.ALL_TIME) return stats

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -timeRange.days)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

        return stats.filter { it.date >= startDate }
    }

    fun setTimeRange(timeRange: TimeRange) {
        _selectedTimeRange.value = timeRange
    }

    class Factory(
        private val statisticsRepository: StatisticsRepository,
        private val todoRepository: TodoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                return StatisticsViewModel(statisticsRepository, todoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
