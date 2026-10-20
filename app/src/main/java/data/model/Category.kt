package com.timetask.watch.data.model

enum class Category(val displayName: String, val colorHex: Long) {
    PERSONAL("Personal", 0xFF96979D),
    UNIVERSIDAD("Universidad", 0xFFC6A56B),
    TRABAJO("Trabajo", 0xFF8FA0B8),
    SALUD("Salud", 0xFF8FAE98),
    OTROS("Otros", 0xFF7E7F85)
}

