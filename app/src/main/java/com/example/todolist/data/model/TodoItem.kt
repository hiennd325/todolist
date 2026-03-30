package com.example.todolist.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = TaskList::class,
            parentColumns = ["id"],
            childColumns = ["taskListId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskListId")]
)
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val isStarred: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.OTHER,
    val deadline: Long? = null,
    val reminderTime: Long? = null,
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val taskListId: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)
