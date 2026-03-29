package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.OTHER,
    val deadline: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
