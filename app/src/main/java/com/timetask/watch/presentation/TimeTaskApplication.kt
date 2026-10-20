package com.timetask.watch

import android.app.Application
import com.timetask.watch.data.local.TimeTaskDatabase
import com.timetask.watch.data.local.UserPreferencesRepository
import com.timetask.watch.data.repository.TaskRepository

class TimeTaskApplication : Application() {
    val database by lazy { TimeTaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
    val preferencesRepository by lazy { UserPreferencesRepository(this) }
}