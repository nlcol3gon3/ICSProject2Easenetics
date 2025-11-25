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
                // Load REAL data from ProgressService
                val realStats = progressService.calculateLearningStats(userId)
                val userAchievements = progressService.getAchievements(userId)
                val weeklyProgressData = progressService.getWeeklyProgress(userId)

                _learningStats.value = realStats
                _achievements.value = userAchievements
                _weeklyProgress.value = weeklyProgressData

            } catch (e: Exception) {
                // Handle error - you might want to set error state
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshProgressData(userId: String) {
        loadProgressData(userId)
    }

    fun getMotivationalMessage(): String {
        return learningStats.value?.let { stats ->
            progressService.getMotivationalMessage(stats)
        } ?: "Start your learning journey today! 🌟"
    }
}