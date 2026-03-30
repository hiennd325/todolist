package com.example.todolist.data.repository

import com.example.todolist.data.local.SubtaskDao
import com.example.todolist.data.model.Subtask
import kotlinx.coroutines.flow.Flow

class SubtaskRepository(private val subtaskDao: SubtaskDao) {
    fun getSubtasksByTodoId(todoId: Int): Flow<List<Subtask>> = subtaskDao.getSubtasksByTodoId(todoId)

    fun getSubtaskById(id: Int): Flow<Subtask?> = subtaskDao.getSubtaskById(id)

    fun getSubtaskCount(todoId: Int): Flow<Int> = subtaskDao.getSubtaskCount(todoId)

    fun getCompletedSubtaskCount(todoId: Int): Flow<Int> = subtaskDao.getCompletedSubtaskCount(todoId)

    suspend fun insert(subtask: Subtask): Long = subtaskDao.insert(subtask)

    suspend fun update(subtask: Subtask) = subtaskDao.update(subtask)

    suspend fun delete(subtask: Subtask) = subtaskDao.delete(subtask)

    suspend fun deleteById(id: Int) = subtaskDao.deleteById(id)

    suspend fun deleteByTodoId(todoId: Int) = subtaskDao.deleteByTodoId(todoId)
}
