package com.example.todolist.data.local

import androidx.room.TypeConverter
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.RecurrenceType

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.fromString(value)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.fromString(value)

    @TypeConverter
    fun fromRecurrenceType(recurrenceType: RecurrenceType): String = recurrenceType.name

    @TypeConverter
    fun toRecurrenceType(value: String): RecurrenceType = RecurrenceType.fromString(value)
}
