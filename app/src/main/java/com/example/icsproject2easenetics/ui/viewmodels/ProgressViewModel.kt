package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Achievement
import com.example.icsproject2easenetics.data.models.LearningStats
import com.example.icsproject2easenetics.data.models.WeeklyProgress
import com.example.icsproject2easenetics.service.ProgressService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {
    private val progressService = ProgressService()

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
                // Sample data - in real app, fetch from database
                val sampleStats = progressService.calculateLearningStats(
                    completedLessons = 3,
                    quizzesTaken = 2,
                    totalQuizScore = 170, // 85% average
                    totalQuestions = 200,
                    learningTime = 210, // 3.5 hours
                    lastActivity = java.util.Date()
                )

                _learningStats.value = sampleStats
                _achievements.value = progressService.getAchievements(sampleStats)
                _weeklyProgress.value = progressService.getWeeklyProgress()
            } catch (e: Exception) {
                // Handle error
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