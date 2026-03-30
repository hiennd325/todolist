package com.example.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.TodoRepository
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
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth: StateFlow<Calendar> = _currentMonth

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        todoRepository.allTodos
    ) { currentMonth, selectedDate, allTodos ->
        val todosByDate = groupTodosByDate(allTodos)
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

    private fun groupTodosByDate(todos: List<TodoItem>): Map<String, List<TodoItem>> {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return todos.groupBy { todo ->
            val date = Date(todo.createdAt)
            dateFormat.format(date)
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
        private val todoRepository: TodoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(todoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
