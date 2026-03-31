package com.example.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.Subtask
import com.example.todolist.data.model.TaskList
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.SubtaskRepository
import com.example.todolist.data.repository.TaskListRepository
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.data.recurrence.RecurrenceGenerator
import com.example.todolist.notification.NotificationScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
 import kotlinx.coroutines.flow.first
 import kotlinx.coroutines.launch

enum class SortMode {
    CREATED_AT, DEADLINE, PRIORITY, MANUAL
}

enum class FilterMode {
    ALL, BY_CATEGORY, BY_PRIORITY, BY_COMPLETION, STARRED,
    TODAY, UPCOMING, OVERDUE, COMPLETED_TODAY
}

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val taskLists: List<TaskList> = emptyList(),
    val currentTaskListId: Int = 1,
    val searchQuery: String = "",
    val filterMode: FilterMode = FilterMode.ALL,
    val selectedCategory: Category? = null,
    val selectedPriority: Priority? = null,
    val showCompleted: Boolean = true,
    val sortMode: SortMode = SortMode.CREATED_AT,
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val starredCount: Int = 0,
    val subtasks: Map<Int, List<Subtask>> = emptyMap(),
    val subtaskCounts: Map<Int, Pair<Int, Int>> = emptyMap(),
    val selectedTodoIds: Set<Int> = emptySet(),
    val isSelectionMode: Boolean = false
)

class TodoViewModel(
    private val repository: TodoRepository,
    private val taskListRepository: TaskListRepository,
    private val subtaskRepository: SubtaskRepository,
    private val recurrenceGenerator: RecurrenceGenerator,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterMode = MutableStateFlow(FilterMode.TODAY)
    val filterMode: StateFlow<FilterMode> = _filterMode

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _showCompleted = MutableStateFlow(true)
    private val _sortMode = MutableStateFlow(SortMode.CREATED_AT)
    private val _currentTaskListId = MutableStateFlow(1)

    private val _selectedTodoIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _isSelectionMode = MutableStateFlow(false)

    val currentTaskListId: StateFlow<Int> = _currentTaskListId

    val taskLists: StateFlow<List<TaskList>> = taskListRepository.allTaskLists.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _todos = combine(
        _searchQuery,
        _filterMode,
        _selectedCategory,
        _selectedPriority,
        _sortMode,
        _currentTaskListId
    ) { flows: Array<*> ->
        FilterParams(
            flows[0] as String,
            flows[1] as FilterMode,
            flows[2] as Category?,
            flows[3] as Priority?,
            flows[4] as SortMode,
            flows[5] as Int
        )
    }.flatMapLatest { params ->
        when {
            params.query.isNotEmpty() -> repository.searchTodos(params.query)
            params.filter == FilterMode.STARRED -> repository.getStarredTodos()
            params.filter == FilterMode.BY_CATEGORY && params.category != null ->
                repository.getTodosByCategory(params.category)
            params.filter == FilterMode.BY_PRIORITY && params.priority != null ->
                repository.getTodosByPriority(params.priority)
            params.filter == FilterMode.TODAY -> repository.getTodosDueToday()
            params.filter == FilterMode.UPCOMING -> repository.getUpcomingTodos()
            params.filter == FilterMode.OVERDUE -> repository.getOverdueTodos()
            params.filter == FilterMode.COMPLETED_TODAY -> repository.getCompletedToday()
            else -> when (params.sort) {
                SortMode.DEADLINE -> repository.getTodosSortedByDeadline()
                SortMode.PRIORITY -> repository.getTodosSortedByPriority()
                SortMode.CREATED_AT, SortMode.MANUAL -> repository.getTodosByTaskList(params.taskListId)
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
        _currentTaskListId,
        taskLists,
        repository.todoCount,
        repository.completedCount,
        repository.starredCount,
        _selectedTodoIds,
        _isSelectionMode
    ) { flows ->
        val todos = flows[0] as List<TodoItem>
        val query = flows[1] as String
        val filter = flows[2] as FilterMode
        val category = flows[3] as Category?
        val priority = flows[4] as Priority?
        val showCompleted = flows[5] as Boolean
        val sort = flows[6] as SortMode
        val taskListId = flows[7] as Int
        val taskLists = flows[8] as List<TaskList>
        val total = flows[9] as Int
        val completed = flows[10] as Int
        val starred = flows[11] as Int
        val selectedIds = flows[12] as Set<Int>
        val isSelectionMode = flows[13] as Boolean

        val filteredTodos = if (showCompleted) {
            todos
        } else {
            todos.filter { !it.isCompleted }
        }

        TodoUiState(
            todos = filteredTodos,
            taskLists = taskLists,
            currentTaskListId = taskListId,
            searchQuery = query,
            filterMode = filter,
            selectedCategory = category,
            selectedPriority = priority,
            showCompleted = showCompleted,
            sortMode = sort,
            totalCount = total,
            completedCount = completed,
            starredCount = starred,
            selectedTodoIds = selectedIds,
            isSelectionMode = isSelectionMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoUiState()
    )

    private val _subtasks = MutableStateFlow<Map<Int, List<Subtask>>>(emptyMap())
    val subtasks: StateFlow<Map<Int, List<Subtask>>> = _subtasks

    fun loadSubtasks(todoId: Int) {
        viewModelScope.launch {
            subtaskRepository.getSubtasksByTodoId(todoId).collect { subs ->
                _subtasks.value = _subtasks.value.toMutableMap().apply {
                    put(todoId, subs)
                }
            }
        }
    }

    fun addSubtask(todoId: Int, title: String) {
        viewModelScope.launch {
            subtaskRepository.insert(Subtask(todoId = todoId, title = title))
        }
    }

    fun toggleSubtask(subtask: Subtask) {
        viewModelScope.launch {
            subtaskRepository.update(subtask.copy(isCompleted = !subtask.isCompleted))
        }
    }

    fun deleteSubtask(subtask: Subtask) {
        viewModelScope.launch {
            subtaskRepository.delete(subtask)
        }
    }

    fun updateSubtask(subtask: Subtask) {
        viewModelScope.launch {
            subtaskRepository.update(subtask)
        }
    }

    fun setCurrentTaskList(taskListId: Int) {
        _currentTaskListId.value = taskListId
    }

    fun addTaskList(name: String, color: String = "#00897B", icon: String = "list") {
        viewModelScope.launch {
            taskListRepository.insert(TaskList(name = name, color = color, icon = icon))
        }
    }

    fun updateTaskList(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.update(taskList.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteTaskList(taskList: TaskList) {
        viewModelScope.launch {
            taskListRepository.delete(taskList)
        }
    }

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
        deadline: Long? = null,
        reminderTime: Long? = null,
        recurrenceType: RecurrenceType = RecurrenceType.NONE,
        estimatedDurationMinutes: Int? = null,
        isStarred: Boolean = false,
        taskListId: Int = _currentTaskListId.value
    ) {
        viewModelScope.launch {
            val insertedId = repository.insert(
                TodoItem(
                    title = title,
                    description = description,
                    priority = priority,
                    category = category,
                    deadline = deadline,
                    reminderTime = reminderTime,
                    recurrenceType = recurrenceType,
                    estimatedDurationMinutes = estimatedDurationMinutes,
                    isStarred = isStarred,
                    taskListId = taskListId
                )
            )
            // Generate instances if recurring
            if (recurrenceType != RecurrenceType.NONE) {
                val parentTodo = repository.getTodoByIdSync(insertedId.toInt())
                parentTodo?.let {
                    recurrenceGenerator.generateInstances(it)
                    // Schedule reminders for the generated instances
                    val instances = repository.getInstancesByParentId(it.id).first()
                    instances.forEach { instance ->
                        instance.reminderTime?.let { reminder ->
                            if (reminder > System.currentTimeMillis()) {
                                notificationScheduler.scheduleReminder(instance)
                            }
                        }
                    }
                }
            } else {
                // Schedule reminder for non-recurring task if exists
                if (reminderTime != null && reminderTime > System.currentTimeMillis()) {
                    val todo = repository.getTodoByIdSync(insertedId.toInt())
                    todo?.let { notificationScheduler.scheduleReminder(it) }
                }
            }
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            val oldTodo = repository.getTodoByIdSync(todo.id)
            repository.update(todo.copy(updatedAt = System.currentTimeMillis()))

            // Cancel old reminder for this todo if it had one
            if (oldTodo?.reminderTime != null) {
                notificationScheduler.cancelReminder(todo.id)
            }

            // Handle recurrence changes
            if (todo.recurrenceType != RecurrenceType.NONE) {
                val recurrenceChanged = oldTodo?.recurrenceType != todo.recurrenceType ||
                                       oldTodo?.deadline != todo.deadline
                val existingCount = repository.getInstancesCount(todo.id).first()
                
                if (existingCount == 0 || recurrenceChanged) {
                    // Delete old instances and regenerate
                    recurrenceGenerator.deleteInstances(todo.id)
                    recurrenceGenerator.generateInstances(todo)
                    // Schedule reminders for new instances
                    val instances = repository.getInstancesByParentId(todo.id).first()
                    instances.forEach { instance ->
                        instance.reminderTime?.let { reminder ->
                            if (reminder > System.currentTimeMillis()) {
                                notificationScheduler.scheduleReminder(instance)
                            }
                        }
                    }
                } else {
                    // Instances exist but recurrence settings may have changed on parent; could update their reminders if needed
                    // For simplicity, instances keep their own reminderTimes which were copied at generation
                }
            } else {
                // Changed from recurring to non-recurring: delete instances
                if (oldTodo?.recurrenceType != RecurrenceType.NONE) {
                    // Cancel reminders for all instances before deleting
                    val instances = repository.getInstancesByParentId(todo.id).first()
                    instances.forEach { instance ->
                        if (instance.reminderTime != null) {
                            notificationScheduler.cancelReminder(instance.id)
                        }
                    }
                    recurrenceGenerator.deleteInstances(todo.id)
                }
                // Schedule reminder for the non-recurring todo if updated reminderTime exists
                todo.reminderTime?.let { reminder ->
                    if (reminder > System.currentTimeMillis()) {
                        notificationScheduler.scheduleReminder(todo)
                    }
                }
            }
        }
    }

    fun toggleComplete(todo: TodoItem) {
        viewModelScope.launch {
            repository.update(todo.copy(isCompleted = !todo.isCompleted, updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleStarred(todo: TodoItem) {
        viewModelScope.launch {
            repository.update(todo.copy(isStarred = !todo.isStarred, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            // Cancel reminder if exists
            if (todo.reminderTime != null) {
                notificationScheduler.cancelReminder(todo.id)
            }
            repository.delete(todo)
            // If this is a recurring parent, also delete its instances and cancel their reminders
            if (todo.recurrenceType != RecurrenceType.NONE && !todo.isRecurringInstance) {
                val instances = repository.getInstancesByParentId(todo.id).first()
                instances.forEach { instance ->
                    if (instance.reminderTime != null) {
                        notificationScheduler.cancelReminder(instance.id)
                    }
                }
                recurrenceGenerator.deleteInstances(todo.id)
            }
        }
    }

    fun deleteTodoById(id: Int) {
        viewModelScope.launch {
            val todo = repository.getTodoByIdSync(id)
            if (todo != null) {
                if (todo.reminderTime != null) {
                    notificationScheduler.cancelReminder(todo.id)
                }
                if (todo.recurrenceType != RecurrenceType.NONE && !todo.isRecurringInstance) {
                    val instances = repository.getInstancesByParentId(todo.id).first()
                    instances.forEach { instance ->
                        if (instance.reminderTime != null) {
                            notificationScheduler.cancelReminder(instance.id)
                        }
                    }
                    recurrenceGenerator.deleteInstances(todo.id)
                }
            }
            repository.deleteById(id)
        }
    }

    fun updateSortOrder(id: Int, sortOrder: Int) {
        viewModelScope.launch {
            repository.updateSortOrder(id, sortOrder)
        }
    }

    fun moveTodoToTaskList(todo: TodoItem, taskListId: Int) {
        viewModelScope.launch {
            repository.update(todo.copy(taskListId = taskListId, updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleSelectionMode() {
        _isSelectionMode.value = !_isSelectionMode.value
        if (!_isSelectionMode.value) {
            _selectedTodoIds.value = emptySet()
        }
    }

    fun toggleTodoSelection(todoId: Int) {
        val currentSelection = _selectedTodoIds.value
        _selectedTodoIds.value = if (todoId in currentSelection) {
            currentSelection - todoId
        } else {
            currentSelection + todoId
        }
    }

    fun selectAllTodos() {
        val currentTodos = uiState.value.todos
        _selectedTodoIds.value = currentTodos.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedTodoIds.value = emptySet()
    }

    fun deleteSelectedTodos() {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            selectedIds.forEach { id ->
                repository.deleteById(id)
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun completeSelectedTodos() {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            val currentTime = System.currentTimeMillis()
            selectedIds.forEach { id ->
                repository.getTodoByIdSync(id)?.let { todo ->
                    if (!todo.isCompleted) {
                        repository.update(todo.copy(isCompleted = true, updatedAt = currentTime))
                    }
                }
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun moveSelectedTodosToTaskList(taskListId: Int) {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            val currentTime = System.currentTimeMillis()
            selectedIds.forEach { id ->
                repository.getTodoByIdSync(id)?.let { todo ->
                    repository.update(todo.copy(taskListId = taskListId, updatedAt = currentTime))
                }
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun starSelectedTodos() {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            val currentTime = System.currentTimeMillis()
            selectedIds.forEach { id ->
                repository.getTodoByIdSync(id)?.let { todo ->
                    if (!todo.isStarred) {
                        repository.update(todo.copy(isStarred = true, updatedAt = currentTime))
                    }
                }
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun updateSelectedTodosCategory(category: Category) {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            val currentTime = System.currentTimeMillis()
            selectedIds.forEach { id ->
                repository.getTodoByIdSync(id)?.let { todo ->
                    repository.update(todo.copy(category = category, updatedAt = currentTime))
                }
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun updateSelectedTodosPriority(priority: Priority) {
        viewModelScope.launch {
            val selectedIds = _selectedTodoIds.value
            val currentTime = System.currentTimeMillis()
            selectedIds.forEach { id ->
                repository.getTodoByIdSync(id)?.let { todo ->
                    repository.update(todo.copy(priority = priority, updatedAt = currentTime))
                }
            }
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    class Factory(
        private val repository: TodoRepository,
        private val taskListRepository: TaskListRepository,
        private val subtaskRepository: SubtaskRepository,
        private val recurrenceGenerator: RecurrenceGenerator,
        private val notificationScheduler: NotificationScheduler
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
                return TodoViewModel(repository, taskListRepository, subtaskRepository, recurrenceGenerator, notificationScheduler) as T
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
    val sort: SortMode,
    val taskListId: Int
)
