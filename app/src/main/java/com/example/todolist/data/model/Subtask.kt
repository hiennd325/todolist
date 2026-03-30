package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = TodoItem::class,
            parentColumns = ["id"],
            childColumns = ["todoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("todoId")]
)
data class Subtask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val todoId: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
