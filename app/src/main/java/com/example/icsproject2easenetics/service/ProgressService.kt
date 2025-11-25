package com.example.icsproject2easenetics.service

import com.example.icsproject2easenetics.data.models.Achievement
import com.example.icsproject2easenetics.data.models.AchievementType
import com.example.icsproject2easenetics.data.models.LearningStats
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.models.WeeklyProgress
import com.example.icsproject2easenetics.data.repositories.ProgressRepository
import java.util.Date
import java.util.Calendar

class ProgressService {
    private val progressRepository = ProgressRepository()

    suspend fun calculateLearningStats(userId: String): LearningStats {
        return try {
            // Get REAL data from Firebase using fallback methods
            val userProgress = progressRepository.getUserProgress(userId)

            val completedLessons = userProgress.count { it.completed }
            val quizzesTaken = userProgress.count { it.score > 0 }
            val totalQuizScore = userProgress.sumOf { it.score }
            val totalLearningTime = userProgress.sumOf { it.timeSpent }

            val averageScore = if (quizzesTaken > 0) {
                (totalQuizScore.toDouble() / quizzesTaken)
            } else {
                0.0
            }

            val currentStreak = calculateCurrentStreak(userProgress)
            val longestStreak = calculateLongestStreak(userProgress)

            LearningStats(
                userId = userId,
                totalLessonsCompleted = completedLessons,
                totalQuizzesTaken = quizzesTaken,
                averageQuizScore = averageScore,
                totalLearningTime = totalLearningTime,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                lastActivityDate = Date(),
                skillsMastered = calculateSkillsMastered(completedLessons)
            )
        } catch (e: Exception) {
            println("❌ ProgressService: Error calculating learning stats: ${e.message}")
            // Return default stats if error
            LearningStats(userId = userId)
        }
    }

    suspend fun getAchievements(userId: String): List<Achievement> {
        val stats = calculateLearningStats(userId)

        return listOf(
            Achievement(
                achievementId = "first_lesson",
                title = "First Steps",
                description = "Complete your first lesson",
                iconRes = "🎯",
                type = AchievementType.LESSONS,
                target = 1,
                currentProgress = minOf(stats.totalLessonsCompleted, 1),
                unlocked = stats.totalLessonsCompleted >= 1,
                unlockedDate = if (stats.totalLessonsCompleted >= 1) Date() else null
            ),
            Achievement(
                achievementId = "lesson_explorer",
                title = "Lesson Explorer",
                description = "Complete 5 lessons",
                iconRes = "📚",
                type = AchievementType.LESSONS,
                target = 5,
                currentProgress = minOf(stats.totalLessonsCompleted, 5),
                unlocked = stats.totalLessonsCompleted >= 5,
                unlockedDate = null
            ),
            Achievement(
                achievementId = "quiz_master",
                title = "Quiz Master",
                description = "Complete 5 quizzes with 80%+ average score",
                iconRes = "🏆",
                type = AchievementType.QUIZZES,
                target = 5,
                currentProgress = minOf(stats.totalQuizzesTaken, 5),
                unlocked = stats.totalQuizzesTaken >= 5 && stats.averageQuizScore >= 80,
                unlockedDate = null
            ),
            Achievement(
                achievementId = "perfect_score",
                title = "Perfect Score",
                description = "Score 100% on any quiz",
                iconRes = "💯",
                type = AchievementType.SCORE,
                target = 100,
                currentProgress = if (stats.averageQuizScore >= 100) 100 else 0,
                unlocked = stats.averageQuizScore >= 100,
                unlockedDate = null
            ),
            Achievement(
                achievementId = "week_warrior",
                title = "Week Warrior",
                description = "Maintain a 7-day learning streak",
                iconRes = "🔥",
                type = AchievementType.STREAK,
                target = 7,
                currentProgress = minOf(stats.currentStreak, 7),
                unlocked = stats.currentStreak >= 7,
                unlockedDate = null
            ),
            Achievement(
                achievementId = "time_investor",
                title = "Time Investor",
                description = "Spend 5 hours learning",
                iconRes = "⏰",
                type = AchievementType.TIME,
                target = 300, // 5 hours in minutes
                currentProgress = minOf(stats.totalLearningTime, 300),
                unlocked = stats.totalLearningTime >= 300,
                unlockedDate = null
            ),
            Achievement(
                achievementId = "digital_citizen",
                title = "Digital Citizen",
                description = "Complete all modules",
                iconRes = "🌟",
                type = AchievementType.LESSONS,
                target = 20, // Adjust based on your total lessons
                currentProgress = stats.totalLessonsCompleted,
                unlocked = stats.totalLessonsCompleted >= 20,
                unlockedDate = null
            )
        )
    }

    private fun calculateCurrentStreak(userProgress: List<UserProgress>): Int {
        if (userProgress.isEmpty()) return 0

        // Simple streak calculation based on recent activity
        val recentActivities = userProgress.sortedByDescending { it.lastAccessed }
        if (recentActivities.isEmpty()) return 0

        // Count consecutive days with activity (simplified)
        return minOf(userProgress.distinctBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.lastAccessed
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        }.size, 30) // Cap at 30 days
    }

    private fun calculateLongestStreak(userProgress: List<UserProgress>): Int {
        val currentStreak = calculateCurrentStreak(userProgress)
        return maxOf(currentStreak, 3) // Simple calculation for now
    }

    private fun calculateSkillsMastered(completedLessons: Int): List<String> {
        val skills = mutableListOf<String>()
        if (completedLessons >= 1) skills.add("Smartphone Basics")
        if (completedLessons >= 3) skills.add("Internet Safety")
        if (completedLessons >= 5) skills.add("Social Media")
        if (completedLessons >= 7) skills.add("M-Pesa Fundamentals")
        if (completedLessons >= 10) skills.add("Government Services")
        if (completedLessons >= 15) skills.add("Advanced Digital Skills")
        return skills
    }

    fun getMotivationalMessage(stats: LearningStats): String {
        return when {
            stats.currentStreak >= 7 -> "🔥 Amazing streak! You're on fire!"
            stats.averageQuizScore >= 90 -> "💯 Excellent scores! You're mastering digital skills!"
            stats.totalLessonsCompleted >= 10 -> "🚀 Great progress! You've completed ${stats.totalLessonsCompleted} lessons!"
            stats.totalLearningTime >= 60 -> "⏰ You've invested ${stats.totalLearningTime} minutes in learning - great commitment!"
            stats.totalLessonsCompleted >= 5 -> "🌟 You're making great progress! Keep learning!"
            else -> "🌟 Welcome! Start your digital learning journey today!"
        }
    }

    suspend fun getWeeklyProgress(userId: String): List<WeeklyProgress> {
        return emptyList()
    }
}