package com.example.todolist.ui.viewmodel

import android.app.Application
import java.util.Calendar
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todolist.data.model.CalendarDisplayMode
import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.TodoRepository
import com.example.todolist.data.settings.SettingsManager
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.*

class CalendarViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TodoRepository
    private lateinit var settingsManager: SettingsManager
    private lateinit var viewModel: CalendarViewModel

    @Before
    fun setup() {
        repository = mock()
        settingsManager = mock()
        // Mock settingsFlow to return BY_CREATED by default
        whenever(settingsManager.settingsFlow).thenReturn(
            flowOf(
                SettingsManager.Settings(
                    themePreset = com.example.todolist.ui.theme.ThemePreset.Default,
                    darkMode = "system",
                    dynamicColor = false,
                    fontSize = "medium",
                    calendarDisplayMode = CalendarDisplayMode.BY_CREATED
                )
            )
        )
        
        viewModel = CalendarViewModel(repository, settingsManager)
    }

    @Test
    fun `groupTodosByDate with BY_CREATED uses createdAt`() = runTest {
        val method = CalendarViewModel::class.java.getDeclaredMethod("groupTodosByDate", List::class.java, CalendarDisplayMode::class.java)
        method.isAccessible = true
        
        val todos = listOf(
            TodoItem(title = "Task 1", createdAt = 1711708800000), // 2024-03-30
            TodoItem(title = "Task 2", createdAt = 1711795200000)  // 2024-03-31
        )
        
        @Suppress("UNCHECKED_CAST")
        val result = method.invoke(viewModel, todos, CalendarDisplayMode.BY_CREATED) as Map<String, List<TodoItem>>
        
        val date1 = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(1711708800000))
        val date2 = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(1711795200000))
        assertTrue(result.containsKey(date1))
        assertTrue(result.containsKey(date2))
        assertEquals(1, result[date1]?.size)
        assertEquals(1, result[date2]?.size)
    }

    @Test
    fun `groupTodosByDate with BY_DEADLINE uses occurrenceDate for instances`() = runTest {
        val method = CalendarViewModel::class.java.getDeclaredMethod("groupTodosByDate", List::class.java, CalendarDisplayMode::class.java)
        method.isAccessible = true
        
        val now = System.currentTimeMillis()
        val parent = TodoItem(
            id = 1,
            title = "Parent",
            recurrenceType = RecurrenceType.DAILY,
            deadline = now + 86400000 // tomorrow
        )
        val instance = TodoItem(
            id = 2,
            title = "Instance",
            isRecurringInstance = true,
            occurrenceDate = now + 2*86400000,
            recurrenceParentId = 1
        )
        val nonRecurring = TodoItem(
            id = 3,
            title = "Non-recurring",
            deadline = now + 3*86400000
        )
        
        val todos = listOf(parent, instance, nonRecurring)
        
        @Suppress("UNCHECKED_CAST")
        val result = method.invoke(viewModel, todos, CalendarDisplayMode.BY_DEADLINE) as Map<String, List<TodoItem>>
        
        // Extract date strings from timestamps
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val parentDate = dateFormat.format(java.util.Date(parent.deadline!!))
        val instanceDate = dateFormat.format(java.util.Date(instance.occurrenceDate!!))
        val nonRecurringDate = dateFormat.format(java.util.Date(nonRecurring.deadline!!))
        
        assertTrue(result.containsKey(parentDate))
        assertTrue(result.containsKey(instanceDate))
        assertTrue(result.containsKey(nonRecurringDate))
    }

    @Test
    fun `groupTodosByDate with BY_DEADLINE falls back to createdAt for tasks without deadline`() = runTest {
        val method = CalendarViewModel::class.java.getDeclaredMethod("groupTodosByDate", List::class.java, CalendarDisplayMode::class.java)
        method.isAccessible = true
        
        val now = System.currentTimeMillis()
        val todo = TodoItem(
            id = 1,
            title = "No deadline",
            createdAt = now - 86400000 // yesterday
        )
        
        @Suppress("UNCHECKED_CAST")
        val result = method.invoke(viewModel, listOf(todo), CalendarDisplayMode.BY_DEADLINE) as Map<String, List<TodoItem>>
        
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val expectedDate = dateFormat.format(java.util.Date(todo.createdAt))
        assertTrue(result.containsKey(expectedDate))
    }

    @Test
    fun `selectDate toggles date selection`() = runTest {
        val testDate = "2024-04-15"
        assertNull(viewModel.selectedDate.value)
        
        viewModel.selectDate(testDate)
        assertEquals(testDate, viewModel.selectedDate.value)
        
        viewModel.selectDate(testDate) // toggle off
        assertNull(viewModel.selectedDate.value)
    }

    @Test
    fun `goToToday sets currentMonth and selectedDate to today`() = runTest {
        viewModel.goToToday()
        
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val currentMonth = viewModel.currentMonth.value
        val now = Calendar.getInstance()
        
        assertEquals(now.get(Calendar.YEAR), currentMonth.get(Calendar.YEAR))
        assertEquals(now.get(Calendar.MONTH), currentMonth.get(Calendar.MONTH))
        assertEquals(today, viewModel.selectedDate.value)
    }

    @Test
    fun `nextMonth increments month`() = runTest {
        val initialMonth = viewModel.currentMonth.value.clone() as Calendar
        val initialMonthValue = initialMonth.get(Calendar.MONTH)
        val initialYear = initialMonth.get(Calendar.YEAR)
        
        viewModel.nextMonth()
        
        val newMonth = viewModel.currentMonth.value
        val expectedMonth = (initialMonthValue + 1) % 12
        val expectedYear = if (initialMonthValue == Calendar.DECEMBER) initialYear + 1 else initialYear
        
        assertEquals(expectedMonth, newMonth.get(Calendar.MONTH))
        assertEquals(expectedYear, newMonth.get(Calendar.YEAR))
    }

    @Test
    fun `previousMonth decrements month`() = runTest {
        // Setup a known month, e.g., March 2024
        val cal = Calendar.getInstance().apply {
            set(2024, Calendar.MARCH, 1)
        }
        // We need to set _currentMonth directly via reflection? Actually use the method
        // Instead, we can call methods to set state. But ViewModel has private _currentMonth.
        // Workaround: use goToToday then nextMonth/previousMonth etc.
        // For test simplicity, we just test that previousMonth changes value.
        // Better: we can use reflection to set _currentMonth. 
        val field = CalendarViewModel::class.java.getDeclaredField("_currentMonth")
        field.isAccessible = true
        (field.get(viewModel) as? kotlinx.coroutines.flow.MutableStateFlow<Calendar>)?.value = cal
        
        viewModel.previousMonth()
        val newMonth = viewModel.currentMonth.value
        
        assertEquals(Calendar.FEBRUARY, newMonth.get(Calendar.MONTH))
        assertEquals(2024, newMonth.get(Calendar.YEAR))
    }
}