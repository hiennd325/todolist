package com.example.todolist.data.repository

import com.example.todolist.data.local.CustomCategoryDao
import com.example.todolist.data.model.CustomCategory
import kotlinx.coroutines.flow.Flow

class CustomCategoryRepository(private val customCategoryDao: CustomCategoryDao) {
    val allCategories: Flow<List<CustomCategory>> = customCategoryDao.getAll()

    fun getById(id: Int): Flow<CustomCategory?> = customCategoryDao.getById(id)

    suspend fun insert(category: CustomCategory): Long = customCategoryDao.insert(category)

    suspend fun update(category: CustomCategory) = customCategoryDao.update(category)

    suspend fun delete(category: CustomCategory) = customCategoryDao.delete(category)

    suspend fun deleteById(id: Int) = customCategoryDao.deleteById(id)
}
