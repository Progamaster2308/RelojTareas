package com.timetask.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val scheduledTime: LocalDateTime,
    val category: Category = Category.PERSONAL,
    val priority: Priority = Priority.NORMAL,
    val isCompleted: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val notifyUser: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)