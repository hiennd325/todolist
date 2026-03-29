package com.example.todolist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    fun getTodoById(id: Int): Flow<TodoItem?>

    @Query("SELECT * FROM todo_items WHERE category = :category ORDER BY createdAt DESC")
    fun getTodosByCategory(category: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTodosByPriority(priority: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchTodos(query: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = :completed ORDER BY createdAt DESC")
    fun getTodosByCompletion(completed: Boolean): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items ORDER BY CASE WHEN deadline IS NULL THEN 1 ELSE 0 END, deadline ASC")
    fun getTodosSortedByDeadline(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items ORDER BY CASE priority WHEN 'HIGH' THEN 3 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 1 END DESC")
    fun getTodosSortedByPriority(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items ORDER BY createdAt DESC")
    fun getTodosSortedByCreatedAt(): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoItem)

    @Update
    suspend fun update(todo: TodoItem)

    @Delete
    suspend fun delete(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM todo_items")
    fun getTodoCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>
}
