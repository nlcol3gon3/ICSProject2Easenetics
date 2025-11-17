package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.repositories.LessonRepository
import com.example.icsproject2easenetics.data.repositories.ProgressRepository
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    // Create repository instances with proper parameters
    private val userRepository = UserRepository(FirebaseFirestore.getInstance())
    private val lessonRepository = LessonRepository()
    private val progressRepository = ProgressRepository()

    private val _userProgress = MutableStateFlow<List<UserProgress>>(emptyList())
    val userProgress: StateFlow<List<UserProgress>> = _userProgress.asStateFlow()

    private val _availableLessons = MutableStateFlow<List<Lesson>>(emptyList())
    val availableLessons: StateFlow<List<Lesson>> = _availableLessons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadUserProgress(userId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Load real progress from Firebase
                val progress = progressRepository.getUserProgress(userId)
                _userProgress.value = progress

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load progress: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvailableLessons() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Load ALL lessons from Firebase across all modules
                val allLessons = mutableListOf<Lesson>()

                // Get lessons from each module
                val modules = lessonRepository.getAllModules()
                modules.forEach { module ->
                    val lessons = lessonRepository.getLessonsByModule(module.moduleId)
                    allLessons.addAll(lessons)
                }

                _availableLessons.value = allLessons

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load lessons: ${e.message}"
                _availableLessons.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLessonProgress(progress: UserProgress) {
        viewModelScope.launch {
            try {
                // Save to Firebase
                val result = progressRepository.saveUserProgress(progress)

                if (result.isSuccess) {
                    // Update local state
                    val currentProgress = _userProgress.value.toMutableList()
                    val existingIndex = currentProgress.indexOfFirst { it.lessonId == progress.lessonId }

                    if (existingIndex >= 0) {
                        currentProgress[existingIndex] = progress
                    } else {
                        currentProgress.add(progress)
                    }

                    _userProgress.value = currentProgress
                } else {
                    _errorMessage.value = "Failed to save progress to database"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update progress: ${e.message}"
            }
        }
    }

    fun markLessonComplete(userId: String, lessonId: String, score: Int = 0, timeSpent: Int = 0) {
        viewModelScope.launch {
            try {
                val result = progressRepository.markLessonComplete(userId, lessonId, score, timeSpent)

                if (result.isSuccess) {
                    // Reload progress to get the updated data
                    loadUserProgress(userId)
                } else {
                    _errorMessage.value = "Failed to mark lesson as complete"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to mark lesson complete: ${e.message}"
            }
        }
    }

    fun updateQuizScore(userId: String, lessonId: String, score: Int) {
        viewModelScope.launch {
            try {
                val result = progressRepository.updateQuizScore(userId, lessonId, score)

                if (result.isSuccess) {
                    // Reload progress to get the updated data
                    loadUserProgress(userId)
                } else {
                    _errorMessage.value = "Failed to update quiz score"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update quiz score: ${e.message}"
            }
        }
    }

    fun getProgressForLesson(lessonId: String): UserProgress? {
        return _userProgress.value.find { it.lessonId == lessonId }
    }

    fun getCompletedLessonsCount(): Int {
        return _userProgress.value.count { it.completed }
    }

    fun getAverageScore(): Int {
        val completedLessons = _userProgress.value.filter { it.completed && it.score > 0 }
        return if (completedLessons.isNotEmpty()) {
            completedLessons.map { it.score }.average().toInt()
        } else {
            0
        }
    }

    fun getTotalLearningTime(): Int {
        return _userProgress.value.sumOf { it.timeSpent }
    }
}