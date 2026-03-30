package com.example.todolist.data.model

import androidx.compose.ui.graphics.Color

enum class Priority(val label: String, val level: Int) {
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1);

    companion object {
        fun fromString(value: String): Priority {
            return entries.find { it.name == value } ?: MEDIUM
        }
    }
}

val Priority.color: Color
    get() = when (this) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFFA726)
        Priority.LOW -> Color(0xFF66BB6A)
    }
