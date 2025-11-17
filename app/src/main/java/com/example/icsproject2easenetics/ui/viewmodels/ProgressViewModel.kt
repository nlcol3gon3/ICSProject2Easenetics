// ui/viewmodels/ProgressViewModel.kt
package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Achievement
import com.example.icsproject2easenetics.data.models.LearningStats
import com.example.icsproject2easenetics.data.models.WeeklyProgress
import com.example.icsproject2easenetics.data.repositories.ProgressRepository
import com.example.icsproject2easenetics.service.ProgressService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class ProgressViewModel : ViewModel() {
    private val progressService = ProgressService()
    private val progressRepository = ProgressRepository()

    private val _learningStats = MutableStateFlow<LearningStats?>(null)
    val learningStats: StateFlow<LearningStats?> = _learningStats.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _weeklyProgress = MutableStateFlow<List<WeeklyProgress>>(emptyList())
    val weeklyProgress: StateFlow<List<WeeklyProgress>> = _weeklyProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadProgressData(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Load REAL progress data from Firebase
                val userProgress = progressRepository.getUserProgress(userId)

                // Calculate real statistics from actual user data
                val completedLessons = userProgress.count { it.completed }
                val quizzesTaken = userProgress.count { it.score > 0 }
                val totalQuizScore = userProgress.sumOf { it.score }
                val totalQuestions = if (quizzesTaken > 0) quizzesTaken * 10 else 1 // Estimate 10 questions per quiz
                val totalLearningTime = userProgress.sumOf { it.timeSpent }
                val lastActivity = userProgress.maxByOrNull { it.lastAccessed }?.lastAccessed?.let { Date(it) } ?: Date()

                // Calculate REAL learning stats
                val realStats = progressService.calculateLearningStats(
                    completedLessons = completedLessons,
                    quizzesTaken = quizzesTaken,
                    totalQuizScore = totalQuizScore,
                    totalQuestions = totalQuestions,
                    learningTime = totalLearningTime,
                    lastActivity = lastActivity
                )

                _learningStats.value = realStats
                _achievements.value = progressService.getAchievements(realStats)
                _weeklyProgress.value = progressService.getWeeklyProgress()

            } catch (e: Exception) {
                // Handle error - data will remain as null/empty
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMotivationalMessage(): String {
        return learningStats.value?.let { stats ->
            progressService.getMotivationalMessage(stats)
        } ?: "Start your learning journey today! 🌟"
    }
}