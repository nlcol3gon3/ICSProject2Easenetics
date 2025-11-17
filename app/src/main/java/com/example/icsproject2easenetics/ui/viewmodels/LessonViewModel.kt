package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.repositories.LessonRepository
import com.example.icsproject2easenetics.data.repositories.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel : ViewModel() {
    // Create repository instances directly
    private val lessonRepository = LessonRepository()
    private val progressRepository = ProgressRepository()

    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()

    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)

    fun setCurrentUser(userId: String) {
        _currentUserId.value = userId
    }

    fun loadLesson(lessonId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                println("🔄 LessonViewModel: Loading lesson: $lessonId")

                // Fetch lesson from Firebase
                val lesson = lessonRepository.getLessonById(lessonId)
                _currentLesson.value = lesson
                println("✅ LessonViewModel: Lesson loaded: ${lesson?.title}")

                // Load REAL progress from Firebase
                _currentUserId.value?.let { userId ->
                    val progress = progressRepository.getProgressForLesson(userId, lessonId)
                    _userProgress.value = progress ?: UserProgress(
                        progressId = "${userId}_$lessonId",
                        userId = userId,
                        lessonId = lessonId,
                        completed = false,
                        score = 0,
                        timeSpent = 0,
                        lastAccessed = System.currentTimeMillis()
                    )
                    println("✅ LessonViewModel: Progress loaded: ${_userProgress.value}")
                }

            } catch (e: Exception) {
                println("❌ LessonViewModel: Error loading lesson: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markLessonComplete() {
        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                val lessonId = _currentLesson.value?.lessonId

                if (userId != null && lessonId != null) {
                    println("🔄 LessonViewModel: Marking lesson complete: $lessonId")

                    val result = progressRepository.markLessonComplete(
                        userId = userId,
                        lessonId = lessonId,
                        score = _userProgress.value?.score ?: 0,
                        timeSpent = _userProgress.value?.timeSpent ?: 0
                    )

                    if (result.isSuccess) {
                        // Update local state
                        _userProgress.value = _userProgress.value?.copy(completed = true)
                        println("✅ LessonViewModel: Lesson marked complete successfully")
                    } else {
                        println("❌ LessonViewModel: Failed to mark lesson complete")
                    }
                }
            } catch (e: Exception) {
                println("❌ LessonViewModel: Error marking lesson complete: ${e.message}")
            }
        }
    }

    fun updateQuizScore(score: Int, total: Int) {
        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                val lessonId = _currentLesson.value?.lessonId
                val percentageScore = (score.toDouble() / total * 100).toInt()

                if (userId != null && lessonId != null) {
                    println("🔄 LessonViewModel: Updating quiz score: $percentageScore%")

                    val result = progressRepository.updateQuizScore(
                        userId = userId,
                        lessonId = lessonId,
                        score = percentageScore
                    )

                    if (result.isSuccess) {
                        // Update local state
                        _userProgress.value = _userProgress.value?.copy(score = percentageScore)
                        println("✅ LessonViewModel: Quiz score updated successfully")
                    } else {
                        println("❌ LessonViewModel: Failed to update quiz score")
                    }
                }
            } catch (e: Exception) {
                println("❌ LessonViewModel: Error updating quiz score: ${e.message}")
            }
        }
    }
}