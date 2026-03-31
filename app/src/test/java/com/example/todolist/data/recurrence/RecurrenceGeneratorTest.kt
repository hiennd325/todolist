package com.example.todolist.data.recurrence

import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.TodoRepository
import java.util.Calendar
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class RecurrenceGeneratorTest {

    private lateinit var repository: TodoRepository
    private lateinit var generator: RecurrenceGenerator

    @Before
    fun setup() {
        repository = mock()
        generator = RecurrenceGenerator(repository)
    }

    @Test
    fun `generateInstances with DAILY creates 30 instances`() = runTest {
        val now = System.currentTimeMillis()
        val parent = TodoItem(
            id = 1,
            title = "Daily Task",
            recurrenceType = RecurrenceType.DAILY,
            deadline = now
        )
        
        whenever(repository.getInstancesCount(parent.id)).thenReturn(flowOf(0))
        whenever(repository.insert(any())).thenReturn(1L)
        
        generator.generateInstances(parent)
        
        // Verify 31 inserts (calls for each occurrence including today and 30 days later)
        verify(repository, times(31)).insert(argThat {
            recurrenceParentId == parent.id && isRecurringInstance && occurrenceDate != null
        })
    }

    @Test
    fun `generateInstances with WEEKLY creates approx 4 instances`() = runTest {
        val now = System.currentTimeMillis()
        val parent = TodoItem(
            id = 2,
            title = "Weekly Task",
            recurrenceType = RecurrenceType.WEEKLY,
            deadline = now
        )
        
        whenever(repository.getInstancesCount(parent.id)).thenReturn(flowOf(0))
        whenever(repository.insert(any())).thenReturn(1L)
        
        generator.generateInstances(parent)
        
        // 30 days / 7 ≈ 4.28, so should be 4 or 5
        verify(repository, atLeast(4)).insert(any())
    }

    @Test
    fun `generateInstances does nothing if recurrenceType is NONE`() = runTest {
        val parent = TodoItem(
            id = 3,
            title = "Non-recurring",
            recurrenceType = RecurrenceType.NONE,
            deadline = System.currentTimeMillis()
        )
        
        generator.generateInstances(parent)
        
        verify(repository, never()).insert(any())
    }

    @Test
    fun `generateInstances does nothing if parentTask is already an instance`() = runTest {
        val instance = TodoItem(
            id = 4,
            title = "Instance",
            recurrenceType = RecurrenceType.DAILY,
            isRecurringInstance = true,
            occurrenceDate = System.currentTimeMillis()
        )
        
        generator.generateInstances(instance)
        
        verify(repository, never()).insert(any())
    }

    @Test
    fun `generateInstances is idempotent and does not duplicate`() = runTest {
        val now = System.currentTimeMillis()
        val parent = TodoItem(
            id = 5,
            title = "Daily Task",
            recurrenceType = RecurrenceType.DAILY,
            deadline = now
        )
        
        whenever(repository.getInstancesCount(parent.id)).thenReturn(flowOf(5)) // existing 5
        
        generator.generateInstances(parent)
        
        verify(repository, never()).insert(any())
    }

    @Test
    fun `MONTHLY recurrence on day 31 generates correct occurrences`() = runTest {
        // Start at Jan 31, 2028
        val jan31 = Calendar.getInstance().apply {
            set(2028, Calendar.JANUARY, 31, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val parent = TodoItem(
            id = 6,
            title = "Monthly 31",
            recurrenceType = RecurrenceType.MONTHLY,
            deadline = jan31
        )
        
        whenever(repository.getInstancesCount(parent.id)).thenReturn(flowOf(0))
        whenever(repository.insert(any())).thenReturn(1L)
        
        generator.generateInstances(parent, horizonDays = 150)
        
        // Should generate occurrences for Jan 31, Feb 29 (leap), Mar 31, Apr 30, May 31, etc.
        val captor = argumentCaptor<TodoItem>()
        verify(repository, atLeast(5)).insert(captor.capture())
        val occurrences = captor.allValues.map { it.occurrenceDate!! }
        
        // Check that first occurrence is Jan 31
        val expectedJan = Calendar.getInstance().apply {
            set(2028, Calendar.JANUARY, 31)
        }.timeInMillis
        assertEquals(expectedJan, occurrences[0])
    }

    @Test
    fun `YEARLY recurrence on Feb 29 handles non-leap years`() = runTest {
        // Start at Feb 29, 2028 (leap year)
        val feb29_2028 = Calendar.getInstance().apply {
            set(2028, Calendar.FEBRUARY, 29, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val parent = TodoItem(
            id = 7,
            title = "Yearly Feb 29",
            recurrenceType = RecurrenceType.YEARLY,
            deadline = feb29_2028
        )
        
        whenever(repository.getInstancesCount(parent.id)).thenReturn(flowOf(0))
        whenever(repository.insert(any())).thenReturn(1L)
        
        generator.generateInstances(parent, horizonDays = 750)
        
        val captor = argumentCaptor<TodoItem>()
        verify(repository, atLeast(2)).insert(captor.capture())
        val dates = captor.allValues.map { it.occurrenceDate!! }
        
        // First should be Feb 29 2028
        val expected2028 = Calendar.getInstance().apply {
            set(2028, Calendar.FEBRUARY, 29)
        }.timeInMillis
        assertEquals(expected2028, dates[0])
        
        // Second should be Feb 28 2029 (since 2029 not leap, Calendar.add(YEAR) yields Feb 28)
        val expected2029 = Calendar.getInstance().apply {
            set(2029, Calendar.FEBRUARY, 28)
        }.timeInMillis
        assertEquals(expected2029, dates[1])
    }

    @Test
    fun `deleteInstances removes all associated instances`() = runTest {
        val parentId = 8
        val instances = listOf(
            TodoItem(id = 101, title = "test1", recurrenceParentId = parentId, isRecurringInstance = true),
            TodoItem(id = 102, title = "test2", recurrenceParentId = parentId, isRecurringInstance = true)
        )
        
        whenever(repository.getInstancesByParentId(parentId)).thenReturn(flowOf(instances))
        
        generator.deleteInstances(parentId)
        
        verify(repository).delete(instances[0])
        verify(repository).delete(instances[1])
    }
}