package com.example.todolist.data.model

enum class RecurrenceType(val label: String) {
    NONE("Không lặp"),
    DAILY("Hàng ngày"),
    WEEKLY("Hàng tuần"),
    MONTHLY("Hàng tháng"),
    YEARLY("Hàng năm");

    companion object {
        fun fromString(value: String): RecurrenceType {
            return entries.find { it.name == value } ?: NONE
        }
    }
}
