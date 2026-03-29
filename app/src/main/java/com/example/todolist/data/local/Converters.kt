package com.example.todolist.data.local

import androidx.room.TypeConverter
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.fromString(value)
}
