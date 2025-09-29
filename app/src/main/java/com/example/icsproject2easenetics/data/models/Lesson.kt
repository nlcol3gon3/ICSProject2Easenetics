package com.example.icsproject2easenetics.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey
    val lessonId: String,
    val title: String,
    val description: String,
    val content: String,
    val videoUrl: String? = null,
    val duration: Int, // in minutes
    val difficulty: DifficultyLevel,
    val category: LessonCategory,
    val order: Int
)

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class LessonCategory {
    SMARTPHONE_BASICS, INTERNET_BROWSING, SOCIAL_MEDIA, ONLINE_SAFETY, COMMUNICATION
}