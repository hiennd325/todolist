package com.example.todolist.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(val label: String, val icon: ImageVector) {
    WORK("Công việc", Icons.Default.List),
    PERSONAL("Cá nhân", Icons.Default.Person),
    SHOPPING("Mua sắm", Icons.Default.ShoppingCart),
    HEALTH("Sức khỏe", Icons.Default.Favorite),
    OTHER("Khác", Icons.Default.List);

    companion object {
        fun fromString(value: String): Category {
            return entries.find { it.name == value } ?: OTHER
        }
    }
}
