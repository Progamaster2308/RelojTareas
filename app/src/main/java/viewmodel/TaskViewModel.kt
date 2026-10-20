package com.timetask.watch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timetask.watch.data.model.Category
import com.timetask.watch.data.model.Priority
import com.timetask.watch.data.model.RepeatMode
import com.timetask.watch.data.model.Task
import com.timetask.watch.data.repository.TaskRepository
import com.timetask.watch.scheduling.AlarmScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class TaskUiState(
    val todayTasks: List<Task> = emptyList(),
    val nextUpcomingTask: Task? = null,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val completionRatio: Float = 0f
)

class TaskViewModel(
    private val repository: TaskRepository,
    private val scheduler: AlarmScheduler
) : ViewModel() {

    val uiState: StateFlow<TaskUiState> = repository.getTodayTasks()
        .map { tasks ->
            val completed = tasks.count { it.isCompleted }
            val pending = tasks.count { !it.isCompleted }
            val ratio = if (tasks.isNotEmpty()) completed.toFloat() / tasks.size else 0f
            TaskUiState(
                todayTasks = tasks,
                completedCount = completed,
                pendingCount = pending,
                completionRatio = ratio
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskUiState())

    val nextTask: StateFlow<Task?> = repository.getNextUpcomingTask()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTasks: StateFlow<List<Task>> = repository.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createTask(
        title: String,
        description: String,
        time: LocalDateTime,
        category: Category,
        priority: Priority,
        repeatMode: RepeatMode,
        notify: Boolean
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim(),
                scheduledTime = time,
                category = category,
                priority = priority,
                repeatMode = repeatMode,
                notifyUser = notify
            )
            val id = repository.insertTask(task)
            scheduler.schedule(task.copy(id = id))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            scheduler.cancel(task.id)
            if (!task.isCompleted) {
                scheduler.schedule(task)
            }
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            repository.updateTask(updated)
            if (updated.isCompleted) {
                scheduler.cancel(updated.id)
            } else {
                scheduler.schedule(updated)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            scheduler.cancel(task.id)
            repository.deleteTask(task)
        }
    }

    fun snoozeTask(task: Task, minutes: Int) {
        viewModelScope.launch {
            val updated = task.copy(scheduledTime = LocalDateTime.now().plusMinutes(minutes.toLong()))
            repository.updateTask(updated)
            scheduler.cancel(task.id)
            scheduler.schedule(updated)
        }
    }
}