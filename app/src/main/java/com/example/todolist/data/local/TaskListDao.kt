package com.example.todolist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.model.TaskList
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {
    @Query("SELECT * FROM task_lists ORDER BY sortOrder ASC, createdAt ASC")
    fun getAllTaskLists(): Flow<List<TaskList>>

    @Query("SELECT * FROM task_lists WHERE id = :id")
    fun getTaskListById(id: Int): Flow<TaskList?>

    @Query("SELECT COUNT(*) FROM task_lists")
    fun getTaskListCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskList: TaskList): Long

    @Update
    suspend fun update(taskList: TaskList)

    @Delete
    suspend fun delete(taskList: TaskList)

    @Query("DELETE FROM task_lists WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM todo_items WHERE taskListId = :taskListId")
    fun getTodoCountByTaskList(taskListId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM todo_items WHERE taskListId = :taskListId AND isCompleted = 1")
    fun getCompletedCountByTaskList(taskListId: Int): Flow<Int>
}
