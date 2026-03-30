package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_lists")
data class TaskList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String = "#00897B", // Default teal color
    val icon: String = "list", // Icon name
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
