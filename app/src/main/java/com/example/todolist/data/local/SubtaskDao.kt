package com.example.todolist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.model.Subtask
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {
    @Query("SELECT * FROM subtasks WHERE todoId = :todoId ORDER BY sortOrder ASC, createdAt ASC")
    fun getSubtasksByTodoId(todoId: Int): Flow<List<Subtask>>

    @Query("SELECT * FROM subtasks WHERE id = :id")
    fun getSubtaskById(id: Int): Flow<Subtask?>

    @Query("SELECT COUNT(*) FROM subtasks WHERE todoId = :todoId")
    fun getSubtaskCount(todoId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM subtasks WHERE todoId = :todoId AND isCompleted = 1")
    fun getCompletedSubtaskCount(todoId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subtask: Subtask): Long

    @Update
    suspend fun update(subtask: Subtask)

    @Delete
    suspend fun delete(subtask: Subtask)

    @Query("DELETE FROM subtasks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM subtasks WHERE todoId = :todoId")
    suspend fun deleteByTodoId(todoId: Int)
}
