package com.example.todolist.data.export

import android.content.Context
import android.net.Uri
import com.example.todolist.data.model.Category
import com.example.todolist.data.model.Priority
import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.Subtask
import com.example.todolist.data.model.TaskList
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.SubtaskRepository
import com.example.todolist.data.repository.TaskListRepository
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class ExportImportManager(
    private val context: Context,
    private val todoRepository: TodoRepository,
    private val taskListRepository: TaskListRepository,
    private val subtaskRepository: SubtaskRepository
) {
    suspend fun exportToJson(uri: Uri): Boolean {
        return try {
            val taskLists = taskListRepository.allTaskLists.first()
            val todos = todoRepository.allTodos.first()

            val root = JSONObject()

            // Export task lists
            val taskListsArray = JSONArray()
            taskLists.forEach { taskList ->
                val obj = JSONObject().apply {
                    put("id", taskList.id)
                    put("name", taskList.name)
                    put("color", taskList.color)
                    put("icon", taskList.icon)
                    put("createdAt", taskList.createdAt)
                    put("updatedAt", taskList.updatedAt)
                    put("sortOrder", taskList.sortOrder)
                }
                taskListsArray.put(obj)
            }
            root.put("taskLists", taskListsArray)

            // Export todos with subtasks
            val todosArray = JSONArray()
            todos.forEach { todo ->
                val subtasks = subtaskRepository.getSubtasksByTodoId(todo.id).first()
                val subtasksArray = JSONArray()
                subtasks.forEach { subtask ->
                    val subObj = JSONObject().apply {
                        put("title", subtask.title)
                        put("isCompleted", subtask.isCompleted)
                        put("createdAt", subtask.createdAt)
                        put("sortOrder", subtask.sortOrder)
                    }
                    subtasksArray.put(subObj)
                }

                val obj = JSONObject().apply {
                    put("title", todo.title)
                    put("description", todo.description)
                    put("isCompleted", todo.isCompleted)
                    put("isStarred", todo.isStarred)
                    put("priority", todo.priority.name)
                    put("category", todo.category.name)
                    put("deadline", todo.deadline ?: JSONObject.NULL)
                    put("reminderTime", todo.reminderTime ?: JSONObject.NULL)
                    put("recurrenceType", todo.recurrenceType.name)
                    put("taskListId", todo.taskListId)
                    put("createdAt", todo.createdAt)
                    put("updatedAt", todo.updatedAt)
                    put("sortOrder", todo.sortOrder)
                    put("subtasks", subtasksArray)
                }
                todosArray.put(obj)
            }
            root.put("todos", todosArray)
            root.put("exportedAt", System.currentTimeMillis())
            root.put("version", 1)

            // Write to file
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, "UTF-8").use { writer ->
                    writer.write(root.toString(2))
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importFromJson(uri: Uri): Boolean {
        return try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { reader ->
                    reader.readText()
                }
            } ?: return false

            val root = JSONObject(jsonString)

            // Import task lists
            if (root.has("taskLists")) {
                val taskListsArray = root.getJSONArray("taskLists")
                for (i in 0 until taskListsArray.length()) {
                    val obj = taskListsArray.getJSONObject(i)
                    val taskList = TaskList(
                        name = obj.getString("name"),
                        color = obj.optString("color", "#00897B"),
                        icon = obj.optString("icon", "list"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                        sortOrder = obj.optInt("sortOrder", 0)
                    )
                    taskListRepository.insert(taskList)
                }
            }

            // Import todos
            if (root.has("todos")) {
                val todosArray = root.getJSONArray("todos")
                val taskLists = taskListRepository.allTaskLists.first()
                val defaultTaskListId = taskLists.firstOrNull()?.id ?: 1

                for (i in 0 until todosArray.length()) {
                    val obj = todosArray.getJSONObject(i)
                    val taskListId = obj.optInt("taskListId", defaultTaskListId)
                    
                    // Find corresponding task list (by index if ID doesn't match)
                    val actualTaskListId = if (taskLists.any { it.id == taskListId }) {
                        taskListId
                    } else {
                        defaultTaskListId
                    }

                    val todo = TodoItem(
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        isStarred = obj.optBoolean("isStarred", false),
                        priority = try {
                            Priority.fromString(obj.optString("priority", "MEDIUM"))
                        } catch (e: Exception) {
                            Priority.MEDIUM
                        },
                        category = try {
                            Category.fromString(obj.optString("category", "OTHER"))
                        } catch (e: Exception) {
                            Category.OTHER
                        },
                        deadline = if (obj.isNull("deadline")) null else obj.optLong("deadline"),
                        reminderTime = if (obj.isNull("reminderTime")) null else obj.optLong("reminderTime"),
                        recurrenceType = try {
                            RecurrenceType.fromString(obj.optString("recurrenceType", "NONE"))
                        } catch (e: Exception) {
                            RecurrenceType.NONE
                        },
                        taskListId = actualTaskListId,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
                        sortOrder = obj.optInt("sortOrder", 0)
                    )

                    val todoId = todoRepository.insert(todo)

                    // Import subtasks
                    if (obj.has("subtasks")) {
                        val subtasksArray = obj.getJSONArray("subtasks")
                        for (j in 0 until subtasksArray.length()) {
                            val subObj = subtasksArray.getJSONObject(j)
                            val subtask = Subtask(
                                todoId = todoId.toInt(),
                                title = subObj.getString("title"),
                                isCompleted = subObj.optBoolean("isCompleted", false),
                                createdAt = subObj.optLong("createdAt", System.currentTimeMillis()),
                                sortOrder = subObj.optInt("sortOrder", 0)
                            )
                            subtaskRepository.insert(subtask)
                        }
                    }
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
