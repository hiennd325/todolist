package com.example.todolist.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(val label: String, val icon: ImageVector) {
    WORK("Work", Icons.Default.List),
    PERSONAL("Personal", Icons.Default.Person),
    SHOPPING("Shopping", Icons.Default.ShoppingCart),
    HEALTH("Health", Icons.Default.Favorite),
    OTHER("Other", Icons.Default.List);

    companion object {
        fun fromString(value: String): Category {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}
