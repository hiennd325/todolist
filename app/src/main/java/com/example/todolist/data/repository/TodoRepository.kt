package com.example.todolist.data.repository

import com.example.todolist.data.local.TodoDao
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    val todoCount: Flow<Int> = todoDao.getTodoCount()
    val completedCount: Flow<Int> = todoDao.getCompletedCount()
    val starredCount: Flow<Int> = todoDao.getStarredCount()

    fun getTodoById(id: Int): Flow<TodoItem?> = todoDao.getTodoById(id)

    fun getTodosByTaskList(taskListId: Int): Flow<List<TodoItem>> =
        todoDao.getTodosByTaskList(taskListId)

    fun getStarredTodos(): Flow<List<TodoItem>> = todoDao.getStarredTodos()

    fun getTodosByCategory(category: Category): Flow<List<TodoItem>> =
        todoDao.getTodosByCategory(category.name)

    fun getTodosByPriority(priority: Priority): Flow<List<TodoItem>> =
        todoDao.getTodosByPriority(priority.name)

    fun searchTodos(query: String): Flow<List<TodoItem>> = todoDao.searchTodos(query)

    fun getTodosByCompletion(completed: Boolean): Flow<List<TodoItem>> =
        todoDao.getTodosByCompletion(completed)

    fun getTodosSortedByDeadline(): Flow<List<TodoItem>> = todoDao.getTodosSortedByDeadline()

    fun getTodosSortedByPriority(): Flow<List<TodoItem>> = todoDao.getTodosSortedByPriority()

    fun getTodosSortedByCreatedAt(): Flow<List<TodoItem>> = todoDao.getTodosSortedByCreatedAt()

    fun getTodosWithReminders(currentTime: Long): Flow<List<TodoItem>> =
        todoDao.getTodosWithReminders(currentTime)

    fun getRecurringTodos(): Flow<List<TodoItem>> = todoDao.getRecurringTodos()

    // Smart Lists
    fun getTodosDueToday(endOfDay: Long): Flow<List<TodoItem>> = todoDao.getTodosDueToday(endOfDay)

    fun getOverdueTodos(currentTime: Long): Flow<List<TodoItem>> = todoDao.getOverdueTodos(currentTime)

    fun getUpcomingTodos(startTime: Long, endTime: Long): Flow<List<TodoItem>> =
        todoDao.getUpcomingTodos(startTime, endTime)

    fun getCompletedToday(startOfDay: Long): Flow<List<TodoItem>> = todoDao.getCompletedToday(startOfDay)

    // Smart Lists presets
    fun getTodosDueToday(): Flow<List<TodoItem>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis
        return todoDao.getTodosDueToday(endOfDay)
    }

    fun getOverdueTodos(): Flow<List<TodoItem>> = todoDao.getOverdueTodos(System.currentTimeMillis())

    fun getUpcomingTodos(weekAhead: Long = 7): Flow<List<TodoItem>> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, weekAhead.toInt())
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis
        return todoDao.getUpcomingTodos(System.currentTimeMillis(), endTime)
    }

    fun getCompletedToday(): Flow<List<TodoItem>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        return todoDao.getCompletedToday(startOfDay)
    }

    fun getTodoCountByTaskList(taskListId: Int): Flow<Int> =
        todoDao.getTodoCountByTaskList(taskListId)

    fun getCompletedCountByTaskList(taskListId: Int): Flow<Int> =
        todoDao.getCompletedCountByTaskList(taskListId)

    suspend fun getTodoByIdSync(id: Int): TodoItem? = todoDao.getTodoByIdSync(id)

    suspend fun insert(todo: TodoItem): Long = todoDao.insert(todo)

    suspend fun update(todo: TodoItem) = todoDao.update(todo)

    suspend fun delete(todo: TodoItem) = todoDao.delete(todo)

    suspend fun deleteById(id: Int) = todoDao.deleteById(id)

    suspend fun updateTodos(todos: List<TodoItem>) = todoDao.updateTodos(todos)

    suspend fun deleteTodosByIds(ids: List<Int>) = todoDao.deleteTodosByIds(ids)

    suspend fun updateSortOrder(id: Int, sortOrder: Int) = todoDao.updateSortOrder(id, sortOrder)
    suspend fun deleteInstancesByParentId(parentId: Int) = todoDao.deleteInstancesByParentId(parentId)

    // Recurring Tasks - Parents and Instances
    fun getRecurringParents(): Flow<List<TodoItem>> = todoDao.getRecurringParents()
    
    fun getInstancesByParentId(parentId: Int): Flow<List<TodoItem>> = 
        todoDao.getInstancesByParentId(parentId)
    
    fun getInstancesInDateRange(start: Long, end: Long): Flow<List<TodoItem>> =
        todoDao.getInstancesInDateRange(start, end)
    
    fun getInstancesCount(parentId: Int): Flow<Int> = todoDao.getInstancesCount(parentId)
    
    fun getAllRecurringInstances(): Flow<List<TodoItem>> = todoDao.getAllRecurringInstances()
}
