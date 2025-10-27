package com.example.icsproject2easenetics.data.models

import java.util.Date

data class LearningStats(
    val userId: String = "",
    val totalLessonsCompleted: Int = 0,
    val totalQuizzesTaken: Int = 0,
    val averageQuizScore: Double = 0.0,
    val totalLearningTime: Int = 0, // in minutes
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActivityDate: Date = Date(),
    val skillsMastered: List<String> = emptyList()
) {
    constructor() : this("", 0, 0, 0.0, 0, 0, 0, Date(), emptyList())
}

data class Achievement(
    val achievementId: String = "",
    val title: String = "",
    val description: String = "",
    val iconRes: String = "", // or use drawable resource
    val type: AchievementType = AchievementType.LESSONS,
    val target: Int = 0,
    val currentProgress: Int = 0,
    val unlocked: Boolean = false,
    val unlockedDate: Date? = null
) {
    constructor() : this("", "", "", "", AchievementType.LESSONS, 0, 0, false, null)
}

enum class AchievementType {
    LESSONS, QUIZZES, STREAK, SCORE, TIME
}

data class WeeklyProgress(
    val weekStart: Date = Date(),
    val lessonsCompleted: Int = 0,
    val quizzesTaken: Int = 0,
    val averageScore: Double = 0.0,
    val learningTime: Int = 0
) {
    constructor() : this(Date(), 0, 0, 0.0, 0)
}