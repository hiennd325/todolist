package com.example.todolist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.CategoryStatisticResult
import com.example.todolist.data.model.DailyStatisticResult
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.PriorityStatisticResult
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.model.WeeklyStatisticResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // Get all todos
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    // Get todos by task list
    @Query("SELECT * FROM todo_items WHERE taskListId = :taskListId ORDER BY sortOrder ASC, createdAt DESC")
    fun getTodosByTaskList(taskListId: Int): Flow<List<TodoItem>>

    // Get starred todos
    @Query("SELECT * FROM todo_items WHERE isStarred = 1 ORDER BY createdAt DESC")
    fun getStarredTodos(): Flow<List<TodoItem>>

    // Get todo by ID
    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoById(id: Int): Flow<TodoItem?>

    // Get todos by category
    @Query("SELECT * FROM todo_items WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: String): Flow<List<TodoItem>>

    // Get todos by priority
    @Query("SELECT * FROM todo_items WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTodosByPriority(priority: String): Flow<List<TodoItem>>

    // Search todos
    @Query("SELECT * FROM todo_items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<TodoItem>>

    // Get todos by completion status
    @Query("SELECT * FROM todo_items WHERE isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByCompletion(completed: Boolean): Flow<List<TodoItem>>

    // Get todos sorted by deadline
    @Query("SELECT * FROM todo_items ORDER BY CASE WHEN deadline IS NULL THEN 1 ELSE 0 END, deadline ASC")
    fun getTodosSortedByDeadline(): Flow<List<TodoItem>>

    // Get todos sorted by priority
    @Query("SELECT * FROM todo_items ORDER BY CASE priority WHEN 'HIGH' THEN 3 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 1 END DESC")
    fun getTodosSortedByPriority(): Flow<List<TodoItem>>

    // Get todos sorted by created at
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getTodosSortedByCreatedAt(): Flow<List<TodoItem>>

    // Get todos with reminders
    @Query("SELECT * FROM todo_items WHERE reminderTime IS NOT NULL AND reminderTime > :currentTime ORDER BY reminderTime ASC")
    fun getTodosWithReminders(currentTime: Long): Flow<List<TodoItem>>

    // Get recurring todos
    @Query("SELECT * FROM todo_items WHERE recurrenceType != 'NONE' ORDER BY createdAt DESC")
    fun getRecurringTodos(): Flow<List<TodoItem>>

    // CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoItem): Long

    @Update
    suspend fun update(todo: TodoItem)

    @Delete
    suspend fun delete(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Statistics
    @Query("SELECT COUNT(*) FROM todo_items")
    fun getTodoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE taskListId = :taskListId")
    fun getTodoCountByTaskList(taskListId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE taskListId = :taskListId AND isCompleted = 1")
    fun getCompletedCountByTaskList(taskListId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE isStarred = 1")
    fun getStarredCount(): Flow<Int>

    // Update sort order
    @Query("UPDATE todo_items SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Int, sortOrder: Int)

    // Statistics by category
    @Query("""
        SELECT category, COUNT(*) as total, 
        SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed 
        FROM todo_items GROUP BY category
    """)
    fun getStatisticsByCategory(): Flow<List<CategoryStatisticResult>>

    // Statistics by priority
    @Query("""
        SELECT priority, COUNT(*) as total,
        SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
        FROM todo_items GROUP BY priority
    """)
    fun getStatisticsByPriority(): Flow<List<PriorityStatisticResult>>

    // Daily statistics for last 30 days
    @Query("""
        SELECT date(createdAt/1000, 'unixepoch') as date,
        COUNT(*) as created,
        SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
        FROM todo_items 
        WHERE createdAt >= :startDate
        GROUP BY date(createdAt/1000, 'unixepoch')
        ORDER BY date DESC
    """)
    fun getDailyStatistics(startDate: Long): Flow<List<DailyStatisticResult>>

    // Weekly statistics for last 12 weeks
    @Query("""
        SELECT strftime('%Y-%W', createdAt/1000, 'unixepoch') as week,
        COUNT(*) as created,
        SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
        FROM todo_items
        WHERE createdAt >= :startDate
        GROUP BY week
        ORDER BY week DESC
    """)
    fun getWeeklyStatistics(startDate: Long): Flow<List<WeeklyStatisticResult>>
}
