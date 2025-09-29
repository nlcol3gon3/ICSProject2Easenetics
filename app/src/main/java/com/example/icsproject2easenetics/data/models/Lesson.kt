package com.example.icsproject2easenetics.data.models

data class Lesson(
    val lessonId: String = "",
    val title: String = "",
    val description: String = "",
    val content: String = "",
    val videoUrl: String? = null,
    val duration: Int = 0,
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val category: LessonCategory = LessonCategory.SMARTPHONE_BASICS,
    val order: Int = 0
) {
    constructor() : this("", "", "", "", null, 0, DifficultyLevel.BEGINNER, LessonCategory.SMARTPHONE_BASICS, 0)
}

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class LessonCategory {
    SMARTPHONE_BASICS, INTERNET_BROWSING, SOCIAL_MEDIA, ONLINE_SAFETY, COMMUNICATION
}