package com.timetask.watch.data.local

import androidx.room.TypeConverter
import com.timetask.watch.data.model.Category
import com.timetask.watch.data.model.Priority
import com.timetask.watch.data.model.RepeatMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(value: String): Category = runCatching {
        Category.valueOf(value)
    }.getOrDefault(Category.PERSONAL)

    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = runCatching {
        Priority.valueOf(value)
    }.getOrDefault(Priority.NORMAL)

    @TypeConverter
    fun fromRepeatMode(repeatMode: RepeatMode): String = repeatMode.name

    @TypeConverter
    fun toRepeatMode(value: String): RepeatMode = runCatching {
        RepeatMode.valueOf(value)
    }.getOrDefault(RepeatMode.NONE)
}