package com.example.todolist.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolist.data.model.CustomCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CustomCategory): Long

    @Update
    suspend fun update(category: CustomCategory)

    @Delete
    suspend fun delete(category: CustomCategory)

    @Query("SELECT * FROM custom_categories WHERE id = :id")
    fun getById(id: Int): Flow<CustomCategory?>

    @Query("SELECT * FROM custom_categories ORDER BY createdAt ASC")
    fun getAll(): Flow<List<CustomCategory>>

    @Query("DELETE FROM custom_categories WHERE id = :id")
    suspend fun deleteById(id: Int)
}
