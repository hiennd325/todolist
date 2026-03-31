package com.example.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
 import com.example.todolist.data.model.CalendarDisplayMode
 import com.example.todolist.data.model.TodoItem
 import com.example.todolist.data.repository.TodoRepository
 import com.example.todolist.data.settings.SettingsManager
 import kotlinx.coroutines.flow.*
import java.util.*

data class CalendarUiState(
    val currentMonth: Calendar = Calendar.getInstance(),
    val todosByDate: Map<String, List<TodoItem>> = emptyMap(),
    val selectedDate: String? = null,
    val selectedDateTodos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = true
)

class CalendarViewModel(
    private val todoRepository: TodoRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth: StateFlow<Calendar> = _currentMonth

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    val displayMode: StateFlow<CalendarDisplayMode> = settingsManager.settingsFlow
        .map { it.calendarDisplayMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CalendarDisplayMode.BY_CREATED
        )

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        todoRepository.allTodos,
        displayMode
    ) { currentMonth, selectedDate, allTodos, mode ->
        val todosByDate = groupTodosByDate(allTodos, mode)
        val selectedDateTodos = if (selectedDate != null) {
            todosByDate[selectedDate] ?: emptyList()
        } else {
            emptyList()
        }

        CalendarUiState(
            currentMonth = currentMonth,
            todosByDate = todosByDate,
            selectedDate = selectedDate,
            selectedDateTodos = selectedDateTodos,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    private fun groupTodosByDate(
        todos: List<TodoItem>, 
        mode: CalendarDisplayMode
    ): Map<String, List<TodoItem>> {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return todos.groupBy { todo ->
            val timestamp = when (mode) {
                CalendarDisplayMode.BY_DEADLINE -> {
                    when {
                        todo.isRecurringInstance -> todo.occurrenceDate
                        todo.deadline != null -> todo.deadline
                        else -> todo.createdAt
                    }
                }
                CalendarDisplayMode.BY_CREATED -> todo.createdAt
            }
            dateFormat.format(Date(timestamp ?: 0L))
        }
    }

    fun previousMonth() {
        val newMonth = _currentMonth.value.clone() as Calendar
        newMonth.add(Calendar.MONTH, -1)
        _currentMonth.value = newMonth
    }

    fun nextMonth() {
        val newMonth = _currentMonth.value.clone() as Calendar
        newMonth.add(Calendar.MONTH, 1)
        _currentMonth.value = newMonth
    }

    fun selectDate(date: String) {
        _selectedDate.value = if (_selectedDate.value == date) null else date
    }

    fun goToToday() {
        _currentMonth.value = Calendar.getInstance()
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        _selectedDate.value = today
    }

    class Factory(
        private val todoRepository: TodoRepository,
        private val settingsManager: SettingsManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(todoRepository, settingsManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
