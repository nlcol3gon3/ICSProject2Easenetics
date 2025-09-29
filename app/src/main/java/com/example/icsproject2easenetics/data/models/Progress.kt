package com.example.icsproject2easenetics.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey
    val progressId: String,
    val userId: String,
    val lessonId: String,
    var completed: Boolean = false,
    var score: Int = 0,
    var timeSpent: Int = 0, // in seconds
    val lastAccessed: Long = System.currentTimeMillis()
)