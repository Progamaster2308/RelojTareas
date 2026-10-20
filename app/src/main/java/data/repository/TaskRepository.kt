package com.timetask.watch.data.repository

import com.timetask.watch.data.local.TaskDao
import com.timetask.watch.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    fun getTodayTasks(): Flow<List<Task>> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.atTime(LocalTime.MAX)
        return taskDao.getTasksForRange(startOfDay, endOfDay)
    }

    fun getNextUpcomingTask(): Flow<Task?> = taskDao.getNextUpcomingTask(LocalDateTime.now())

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task.copy(updatedAt = LocalDateTime.now()))

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun getAllPendingTasks(): List<Task> = taskDao.getAllPendingTasksSync()
}