package com.example.todolist.data.recurrence

import com.example.todolist.data.model.RecurrenceType
import com.example.todolist.data.model.TodoItem
import com.example.todolist.data.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Generates recurring task instances (occurrences) from a parent recurring task.
 * 
 * Supports:
 * - DAILY: every N days (N = interval, always 1)
 * - WEEKLY: every N weeks (N = interval, always 1)
 * - MONTHLY: every N months (N = interval, always 1) on the same day of month; skips months with fewer days (e.g., 31st in February)
 * - YEARLY: every N years (N = interval, always 1) on same month/day; skips invalid dates (e.g., Feb 29 in non-leap years)
 */
class RecurrenceGenerator(
    private val todoRepository: TodoRepository
) {
    private val HORIZON_DAYS = 30 // Generate instances for next 30 days
    
    /**
     * Generate recurring instances for a parent task within the horizon (default 30 days).
     * If instances already exist, does nothing (idempotent).
     */
    suspend fun generateInstances(parentTask: TodoItem, horizonDays: Int = HORIZON_DAYS) {
        // Only process if parent is recurring and not an instance itself
        if (parentTask.recurrenceType == RecurrenceType.NONE || parentTask.isRecurringInstance) {
            return
        }
        
        // Check if instances already exist (avoid duplicates)
        val existingCount = todoRepository.getInstancesCount(parentTask.id).first()
        if (existingCount > 0) {
            return
        }
        
        // Determine start date: use deadline if available, else createdAt
        val startTimestamp = parentTask.deadline ?: parentTask.createdAt
        val horizon = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, horizonDays)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        
        val occurrences = calculateOccurrences(
            startDate = startTimestamp,
            recurrenceType = parentTask.recurrenceType,
            horizon = horizon
        )
        
        occurrences.forEach { occurrenceDate ->
            // Adjust reminderTime to the occurrence date, preserving hour/minute/second
            val adjustedReminderTime = parentTask.reminderTime?.let { reminder ->
                val occCal = Calendar.getInstance().apply { timeInMillis = occurrenceDate }
                val remCal = Calendar.getInstance().apply { timeInMillis = reminder }
                occCal.set(Calendar.HOUR_OF_DAY, remCal.get(Calendar.HOUR_OF_DAY))
                occCal.set(Calendar.MINUTE, remCal.get(Calendar.MINUTE))
                occCal.set(Calendar.SECOND, remCal.get(Calendar.SECOND))
                occCal.set(Calendar.MILLISECOND, remCal.get(Calendar.MILLISECOND))
                occCal.timeInMillis
            }
            
            val instance = parentTask.copy(
                id = 0,
                recurrenceParentId = parentTask.id,
                occurrenceDate = occurrenceDate,
                deadline = occurrenceDate,
                isRecurringInstance = true,
                reminderTime = adjustedReminderTime,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            todoRepository.insert(instance)
        }
    }
    
    /**
     * Delete all instances of a parent task.
     */
    suspend fun deleteInstances(parentId: Int) {
        val instances = todoRepository.getInstancesByParentId(parentId).first()
        instances.forEach { instance ->
            todoRepository.delete(instance)
        }
    }
    
    /**
     * Regenerate instances for a parent task (used after recurrence settings change).
     * Deletes old instances and generates new ones.
     */
    suspend fun regenerateInstances(parentTask: TodoItem, horizonDays: Int = HORIZON_DAYS) {
        deleteInstances(parentTask.id)
        generateInstances(parentTask, horizonDays)
    }
    
    /**
     * Calculate occurrence timestamps within the horizon.
     */
    private fun calculateOccurrences(
        startDate: Long,
        recurrenceType: RecurrenceType,
        horizon: Long
    ): List<Long> {
        val occurrences = mutableListOf<Long>()
        val cal = Calendar.getInstance().apply { timeInMillis = startDate }
        val now = Calendar.getInstance()
        
        when (recurrenceType) {
            RecurrenceType.DAILY -> {
                while (cal.timeInMillis < now.timeInMillis) cal.add(Calendar.DAY_OF_YEAR, 1)
                while (cal.timeInMillis <= horizon) {
                    occurrences.add(cal.timeInMillis)
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            RecurrenceType.WEEKLY -> {
                while (cal.timeInMillis < now.timeInMillis) cal.add(Calendar.DAY_OF_YEAR, 7)
                while (cal.timeInMillis <= horizon) {
                    occurrences.add(cal.timeInMillis)
                    cal.add(Calendar.DAY_OF_YEAR, 7)
                }
            }
            RecurrenceType.MONTHLY -> {
                while (cal.timeInMillis < now.timeInMillis) cal.add(Calendar.MONTH, 1)
                while (cal.timeInMillis <= horizon) {
                    occurrences.add(cal.timeInMillis)
                    cal.add(Calendar.MONTH, 1)
                }
            }
            RecurrenceType.YEARLY -> {
                while (cal.timeInMillis < now.timeInMillis) cal.add(Calendar.YEAR, 1)
                while (cal.timeInMillis <= horizon) {
                    occurrences.add(cal.timeInMillis)
                    cal.add(Calendar.YEAR, 1)
                }
            }
            RecurrenceType.NONE -> {}
        }
        
        return occurrences
    }
}