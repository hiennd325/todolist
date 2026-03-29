package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.data.local.TodoDatabase
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.ui.screens.TodoListScreen
import com.example.todolist.ui.theme.TodolistTheme
import com.example.todolist.ui.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TodoDatabase.getDatabase(this)
        val repository = TodoRepository(database.todoDao())
        val viewModel: TodoViewModel = ViewModelProvider(
            this,
            TodoViewModel.Factory(repository)
        )[TodoViewModel::class.java]

        setContent {
            TodolistTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TodoListScreen(viewModel = viewModel)
                }
            }
        }
    }
}
