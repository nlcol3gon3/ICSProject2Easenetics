package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository(FirebaseFirestore.getInstance())

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
        viewModelScope.launch {
            try {
                // Mock data for now
                val mockProgress = listOf(
                    UserProgress(
                        progressId = "1",
                        userId = userId,
                        lessonId = "lesson_0",
                        completed = true,
                        score = 85,
                        timeSpent = 900
                    ),
                    UserProgress(
                        progressId = "2",
                        userId = userId,
                        lessonId = "lesson_1",
                        completed = true,
                        score = 90,
                        timeSpent = 1200
                    )
                )
                _userProgress.value = mockProgress
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load progress: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAvailableLessons() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Mock lessons data
                val mockLessons = listOf(
                    Lesson(
                        lessonId = "lesson_smartphone_basics",
                        title = "Getting Started with Your Smartphone",
                        description = "Learn the basics of using your smartphone",
                        content = "Full lesson content about smartphone basics...",
                        duration = 15,
                        difficulty = com.example.icsproject2easenetics.data.models.DifficultyLevel.BEGINNER,
                        category = com.example.icsproject2easenetics.data.models.LessonCategory.SMARTPHONE_BASICS,
                        order = 1
                    ),
                    Lesson(
                        lessonId = "lesson_internet_safety",
                        title = "Safe Internet Browsing",
                        description = "Stay safe while browsing the internet",
                        content = "Full lesson content about internet safety...",
                        duration = 20,
                        difficulty = com.example.icsproject2easenetics.data.models.DifficultyLevel.BEGINNER,
                        category = com.example.icsproject2easenetics.data.models.LessonCategory.ONLINE_SAFETY,
                        order = 2
                    )
                )
                _availableLessons.value = mockLessons
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load lessons: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this method to your existing UserViewModel.kt file:

    fun updateLessonProgress(progress: UserProgress) {
        viewModelScope.launch {
            try {
                // Update progress in Firestore
                // For now, update local state
                val currentProgress = _userProgress.value.toMutableList()
                val existingIndex = currentProgress.indexOfFirst { it.lessonId == progress.lessonId }

                if (existingIndex >= 0) {
                    currentProgress[existingIndex] = progress
                } else {
                    currentProgress.add(progress)
                }

                _userProgress.value = currentProgress
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update progress: ${e.message}"
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
        val completedLessons = _userProgress.value.filter { it.completed }
        return if (completedLessons.isNotEmpty()) {
            completedLessons.map { it.score }.average().toInt()
        } else {
            0
        }
    }
}