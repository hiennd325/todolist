package com.example.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortMode {
    CREATED_AT, DEADLINE, PRIORITY
}

enum class FilterMode {
    ALL, BY_CATEGORY, BY_PRIORITY, BY_COMPLETION
}

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val searchQuery: String = "",
    val filterMode: FilterMode = FilterMode.ALL,
    val selectedCategory: Category? = null,
    val selectedPriority: Priority? = null,
    val showCompleted: Boolean = true,
    val sortMode: SortMode = SortMode.CREATED_AT,
    val totalCount: Int = 0,
    val completedCount: Int = 0
)

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    val filterMode: StateFlow<FilterMode> = _filterMode

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _showCompleted = MutableStateFlow(true)
    private val _sortMode = MutableStateFlow(SortMode.CREATED_AT)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _todos = combine(
        _searchQuery,
        _filterMode,
        _selectedCategory,
        _selectedPriority,
        _sortMode
    ) { query, filter, category, priority, sort ->
        FilterParams(query, filter, category, priority, sort)
    }.flatMapLatest { params ->
        when {
            params.query.isNotEmpty() -> repository.searchTodos(params.query)
            params.filter == FilterMode.BY_CATEGORY && params.category != null ->
                repository.getTodosByCategory(params.category)
            params.filter == FilterMode.BY_PRIORITY && params.priority != null ->
                repository.getTodosByPriority(params.priority)
            else -> when (params.sort) {
                SortMode.DEADLINE -> repository.getTodosSortedByDeadline()
                SortMode.PRIORITY -> repository.getTodosSortedByPriority()
                SortMode.CREATED_AT -> repository.allTodos
            }
        }
    }

    private val _allTodos = repository.allTodos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val uiState: StateFlow<TodoUiState> = combine(
        _todos,
        _searchQuery,
        _filterMode,
        _selectedCategory,
        _selectedPriority,
        _showCompleted,
        _sortMode,
        repository.todoCount,
        repository.completedCount
    ) { flows ->
        val todos = flows[0] as List<TodoItem>
        val query = flows[1] as String
        val filter = flows[2] as FilterMode
        val category = flows[3] as Category?
        val priority = flows[4] as Priority?
        val showCompleted = flows[5] as Boolean
        val sort = flows[6] as SortMode
        val total = flows[7] as Int
        val completed = flows[8] as Int

        val filteredTodos = if (showCompleted) {
            todos
        } else {
            todos.filter { !it.isCompleted }
        }

        TodoUiState(
            todos = filteredTodos,
            searchQuery = query,
            filterMode = filter,
            selectedCategory = category,
            selectedPriority = priority,
            showCompleted = showCompleted,
            sortMode = sort,
            totalCount = total,
            completedCount = completed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterMode(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun setSelectedCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun setSelectedPriority(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun setShowCompleted(show: Boolean) {
        _showCompleted.value = show
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
    }

    fun addTodo(
        title: String,
        description: String = "",
        priority: Priority = Priority.MEDIUM,
        category: Category = Category.OTHER,
        deadline: Long? = null
    ) {
        viewModelScope.launch {
            repository.insert(
                TodoItem(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    deadline = deadline
                )
            )
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.update(todo.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleComplete(todo: TodoItem) {
        viewModelScope.launch {
            repository.update(todo.copy(isCompleted = !todo.isCompleted, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }

    fun deleteTodoById(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    class Factory(private val repository: TodoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                return TodoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

private data class FilterParams(
    val query: String,
    val filter: FilterMode,
    val category: Category?,
    val priority: Priority?,
    val sort: SortMode
)
