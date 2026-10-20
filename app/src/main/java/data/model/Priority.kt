package com.timetask.watch.data.model

enum class Priority(val displayName: String, val colorHex: Long) {
    NORMAL("Normal", 0xFF96979D),
    IMPORTANTE("Importante", 0xFFC6A56B),
    URGENTE("Urgente", 0xFFCF7777)
}