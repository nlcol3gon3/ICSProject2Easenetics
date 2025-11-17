package com.example.icsproject2easenetics.service

import com.example.icsproject2easenetics.data.models.Achievement
import com.example.icsproject2easenetics.data.models.AchievementType
import com.example.icsproject2easenetics.data.models.LearningStats
import com.example.icsproject2easenetics.data.models.WeeklyProgress
import java.util.Date

class ProgressService {

    fun calculateLearningStats(
        completedLessons: Int,
        quizzesTaken: Int,
        totalQuizScore: Int,
        totalQuestions: Int,
        learningTime: Int,
        lastActivity: Date
    ): LearningStats {
        val averageScore = if (quizzesTaken > 0) {
            (totalQuizScore.toDouble() / quizzesTaken)
        } else {
            0.0
        }

        val currentStreak = calculateCurrentStreak(lastActivity)

        return LearningStats(
            userId = "current_user",
            totalLessonsCompleted = completedLessons,
            totalQuizzesTaken = quizzesTaken,
            averageQuizScore = averageScore,
            totalLearningTime = learningTime,
            currentStreak = currentStreak,
            longestStreak = maxOf(currentStreak, 0),
            lastActivityDate = lastActivity,
            skillsMastered = calculateSkillsMastered(completedLessons)
        )
    }

    fun getAchievements(stats: LearningStats): List<Achievement> {
        return listOf(
            Achievement(
                achievementId = "first_lesson",
                title = "First Steps",
                description = "Complete your first lesson",
                iconRes = "🎯",
                type = AchievementType.LESSONS,
                target = 1,
                currentProgress = minOf(stats.totalLessonsCompleted, 1),
                unlocked = stats.totalLessonsCompleted >= 1
            ),
            Achievement(
                achievementId = "quiz_master",
                title = "Quiz Master",
                description = "Complete 5 quizzes with 80%+ score",
                iconRes = "🏆",
                type = AchievementType.QUIZZES,
                target = 5,
                currentProgress = minOf(stats.totalQuizzesTaken, 5),
                unlocked = stats.totalQuizzesTaken >= 5 && stats.averageQuizScore >= 80
            ),
            Achievement(
                achievementId = "week_warrior",
                title = "Week Warrior",
                description = "Maintain a 7-day learning streak",
                iconRes = "🔥",
                type = AchievementType.STREAK,
                target = 7,
                currentProgress = minOf(stats.currentStreak, 7),
                unlocked = stats.currentStreak >= 7
            ),
            Achievement(
                achievementId = "time_investor",
                title = "Time Investor",
                description = "Spend 5 hours learning",
                iconRes = "⏰",
                type = AchievementType.TIME,
                target = 300, // 5 hours in minutes
                currentProgress = minOf(stats.totalLearningTime, 300),
                unlocked = stats.totalLearningTime >= 300
            ),
            Achievement(
                achievementId = "perfectionist",
                title = "Perfectionist",
                description = "Score 100% on any quiz",
                iconRes = "💯",
                type = AchievementType.SCORE,
                target = 100,
                currentProgress = if (stats.averageQuizScore >= 100) 100 else 0,
                unlocked = stats.averageQuizScore >= 100
            )
        )
    }

    fun getWeeklyProgress(): List<WeeklyProgress> {
        // In a real app, this would fetch from Firebase
        // For now, return empty list or calculate from actual data
        return emptyList()
    }

    private fun calculateCurrentStreak(lastActivity: Date): Int {
        val today = Date()
        val diff = today.time - lastActivity.time
        val daysDiff = (diff / (1000 * 60 * 60 * 24)).toInt()

        return if (daysDiff <= 1) {
            // User was active today or yesterday - continue streak
            // In a real app, you'd calculate actual streak from daily activity log
            3 // Placeholder - replace with actual streak calculation
        } else {
            0 // Streak broken
        }
    }

    private fun calculateSkillsMastered(completedLessons: Int): List<String> {
        val skills = mutableListOf<String>()
        if (completedLessons >= 1) skills.add("Smartphone Basics")
        if (completedLessons >= 2) skills.add("Internet Safety")
        if (completedLessons >= 3) skills.add("Social Media")
        if (completedLessons >= 4) skills.add("M-Pesa Fundamentals")
        if (completedLessons >= 5) skills.add("Government Services")
        return skills
    }

    fun getMotivationalMessage(stats: LearningStats): String {
        return when {
            stats.currentStreak >= 7 -> "🔥 Amazing streak! You're on fire!"
            stats.averageQuizScore >= 90 -> "💯 Excellent scores! You're mastering digital skills!"
            stats.totalLessonsCompleted >= 5 -> "🚀 Great progress! You've completed ${stats.totalLessonsCompleted} lessons!"
            stats.totalLearningTime >= 60 -> "⏰ You've invested ${stats.totalLearningTime} minutes in learning - great commitment!"
            else -> "🌟 Keep going! Every lesson brings you closer to digital confidence!"
        }
    }
}