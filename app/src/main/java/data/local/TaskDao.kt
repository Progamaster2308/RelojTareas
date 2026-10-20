package com.timetask.watch.data.local

import androidx.room.*
import com.timetask.watch.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY scheduledTime ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE scheduledTime >= :startOfDay AND scheduledTime <= :endOfDay ORDER BY scheduledTime ASC")
    fun getTasksForRange(startOfDay: LocalDateTime, endOfDay: LocalDateTime): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 AND scheduledTime >= :now ORDER BY scheduledTime ASC LIMIT 1")
    fun getNextUpcomingTask(now: LocalDateTime): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    suspend fun getAllPendingTasksSync(): List<Task>
}