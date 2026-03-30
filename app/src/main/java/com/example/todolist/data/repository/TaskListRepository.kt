package com.example.todolist.data.repository

import com.example.todolist.data.local.TaskListDao
import com.example.todolist.data.model.TaskList
import kotlinx.coroutines.flow.Flow

class TaskListRepository(private val taskListDao: TaskListDao) {
    val allTaskLists: Flow<List<TaskList>> = taskListDao.getAllTaskLists()
    val taskListCount: Flow<Int> = taskListDao.getTaskListCount()

    fun getTaskListById(id: Int): Flow<TaskList?> = taskListDao.getTaskListById(id)

    fun getTodoCountByTaskList(taskListId: Int): Flow<Int> = taskListDao.getTodoCountByTaskList(taskListId)

    fun getCompletedCountByTaskList(taskListId: Int): Flow<Int> = taskListDao.getCompletedCountByTaskList(taskListId)

    suspend fun insert(taskList: TaskList): Long = taskListDao.insert(taskList)

    suspend fun update(taskList: TaskList) = taskListDao.update(taskList)

    suspend fun delete(taskList: TaskList) = taskListDao.delete(taskList)

    suspend fun deleteById(id: Int) = taskListDao.deleteById(id)
}
