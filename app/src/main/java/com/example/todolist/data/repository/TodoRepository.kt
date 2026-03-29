package com.example.todolist.data.repository

import com.example.todolist.data.local.TodoDao
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    val todoCount: Flow<Int> = todoDao.getTodoCount()
    val completedCount: Flow<Int> = todoDao.getCompletedCount()

    fun getTodoById(id: Int): Flow<TodoItem?> = todoDao.getTodoById(id)

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

    suspend fun insert(todo: TodoItem) = todoDao.insert(todo)

    suspend fun update(todo: TodoItem) = todoDao.update(todo)

    suspend fun delete(todo: TodoItem) = todoDao.delete(todo)

    suspend fun deleteById(id: Int) = todoDao.deleteById(id)
}
