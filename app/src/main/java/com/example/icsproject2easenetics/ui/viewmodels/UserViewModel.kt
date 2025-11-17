// ui/viewmodels/UserViewModel.kt
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
                println("🔄 UserViewModel: Loading REAL user progress for user: $userId")

                // Load real progress from Firebase
                val progress = progressRepository.getUserProgress(userId)
                println("✅ UserViewModel: Loaded ${progress.size} progress records")

                progress.forEach { p ->
                    println("   - Progress: ${p.lessonId} | Completed: ${p.completed} | Score: ${p.score}%")
                }

                _userProgress.value = progress

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load progress: ${e.message}"
                println("❌ UserViewModel: Error loading progress: ${e.message}")
                e.printStackTrace()
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
                println("🔄 UserViewModel: Loading REAL available lessons")

                // Load ALL lessons from Firebase across all modules
                val allLessons = mutableListOf<Lesson>()

                // Get lessons from each module
                val modules = lessonRepository.getAllModules()
                println("📦 UserViewModel: Found ${modules.size} modules")

                modules.forEach { module ->
                    println("   🔍 Loading lessons for module: ${module.moduleId}")
                    val lessons = lessonRepository.getLessonsByModule(module.moduleId)
                    println("   ✅ Found ${lessons.size} lessons for ${module.moduleId}")
                    allLessons.addAll(lessons)
                }

                println("🎯 UserViewModel: Total lessons loaded: ${allLessons.size}")
                _availableLessons.value = allLessons

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load lessons: ${e.message}"
                println("❌ UserViewModel: Error loading lessons: ${e.message}")
                e.printStackTrace()
                _availableLessons.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLessonProgress(progress: UserProgress) {
        viewModelScope.launch {
            try {
                println("🔄 UserViewModel: Saving progress for lesson: ${progress.lessonId}")

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
                    println("✅ UserViewModel: Progress saved successfully")
                } else {
                    _errorMessage.value = "Failed to save progress to database"
                    println("❌ UserViewModel: Failed to save progress")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update progress: ${e.message}"
                println("❌ UserViewModel: Error updating progress: ${e.message}")
            }
        }
    }

    fun markLessonComplete(userId: String, lessonId: String, score: Int = 0, timeSpent: Int = 0) {
        viewModelScope.launch {
            try {
                println("🔄 UserViewModel: Marking lesson complete: $lessonId")

                val result = progressRepository.markLessonComplete(userId, lessonId, score, timeSpent)

                if (result.isSuccess) {
                    // Reload progress to get the updated data
                    loadUserProgress(userId)
                    println("✅ UserViewModel: Lesson marked complete successfully")
                } else {
                    _errorMessage.value = "Failed to mark lesson as complete"
                    println("❌ UserViewModel: Failed to mark lesson complete")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to mark lesson complete: ${e.message}"
                println("❌ UserViewModel: Error marking lesson complete: ${e.message}")
            }
        }
    }

    fun updateQuizScore(userId: String, lessonId: String, score: Int) {
        viewModelScope.launch {
            try {
                println("🔄 UserViewModel: Updating quiz score for $lessonId: $score")

                val result = progressRepository.updateQuizScore(userId, lessonId, score)

                if (result.isSuccess) {
                    // Reload progress to get the updated data
                    loadUserProgress(userId)
                    println("✅ UserViewModel: Quiz score updated successfully")
                } else {
                    _errorMessage.value = "Failed to update quiz score"
                    println("❌ UserViewModel: Failed to update quiz score")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Failed to update quiz score: ${e.message}"
                println("❌ UserViewModel: Error updating quiz score: ${e.message}")
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