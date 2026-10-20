package com.timetask.watch.scheduling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timetask.watch.TimeTaskApplication
import com.timetask.watch.data.model.RepeatMode
import com.timetask.watch.data.model.Task
import com.timetask.watch.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_FIRE = "com.timetask.watch.ACTION_FIRE_TASK_ALARM"
        const val ACTION_COMPLETE = "com.timetask.watch.ACTION_COMPLETE_FROM_NOTIFICATION"
        const val ACTION_SNOOZE = "com.timetask.watch.ACTION_SNOOZE_FROM_NOTIFICATION"
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val app = context.applicationContext as TimeTaskApplication
        val repository = app.repository
        val scheduler = AlarmScheduler(context)
        val notificationHelper = NotificationHelper(context)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = repository.getTaskById(taskId)
                if (task != null) {
                    when (intent.action) {
                        ACTION_FIRE -> {
                            notificationHelper.showTaskNotification(task)
                            // Si es recurrente, planificar la siguiente ocurrencia sin sobreescribir la actual
                            handleRecurrence(task, repository, scheduler)
                        }
                        ACTION_COMPLETE -> {
                            notificationHelper.cancelNotification(taskId)
                            repository.updateTask(task.copy(isCompleted = true))
                        }
                        ACTION_SNOOZE -> {
                            notificationHelper.cancelNotification(taskId)
                            val snoozedTime = LocalDateTime.now().plusMinutes(10)
                            val updatedTask = task.copy(scheduledTime = snoozedTime)
                            repository.updateTask(updatedTask)
                            scheduler.schedule(updatedTask)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleRecurrence(
        task: Task,
        repository: com.timetask.watch.data.repository.TaskRepository,
        scheduler: AlarmScheduler
    ) {
        if (task.repeatMode == RepeatMode.NONE) return

        val nextTime: LocalDateTime = when (task.repeatMode) {
            RepeatMode.DAILY -> task.scheduledTime.plusDays(1)
            RepeatMode.WEEKDAYS -> {
                var candidate = task.scheduledTime.plusDays(1)
                while (candidate.dayOfWeek.value > 5) {
                    candidate = candidate.plusDays(1)
                }
                candidate
            }
            RepeatMode.WEEKLY -> task.scheduledTime.plusWeeks(1)
            RepeatMode.NONE -> task.scheduledTime
        }

        // Crear la próxima instancia en base de datos para no pisar el historial
        val nextOccurrence = task.copy(
            id = 0, // Auto-generar nueva clave primaria
            scheduledTime = nextTime,
            isCompleted = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val newId = repository.insertTask(nextOccurrence)
        scheduler.schedule(nextOccurrence.copy(id = newId))
    }
}