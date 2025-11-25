package com.example.icsproject2easenetics.data.models

// Quiz data
data class Lesson(
    val lessonId: String = "",
    val moduleId: String = "",
    val title: String = "",
    val objective: String = "",
    val description: String = "",
    val content: String = "",
    val videoUrl: String? = null,
    val duration: Int = 0,
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val order: Int = 0,
    val hasQuiz: Boolean = false
) {
    constructor() : this("", "", "", "", "", "", null, 0, DifficultyLevel.BEGINNER, 0, false)
}

// Quiz data class
data class QuizQuestion(
    val questionId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0, // index of correct option
    val explanation: String = ""
) {
    constructor() : this("", "", emptyList(), 0, "")
}
enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}

enum class LessonCategory {
    SMARTPHONE_BASICS, INTERNET_BROWSING, SOCIAL_MEDIA, ONLINE_SAFETY, COMMUNICATION
}