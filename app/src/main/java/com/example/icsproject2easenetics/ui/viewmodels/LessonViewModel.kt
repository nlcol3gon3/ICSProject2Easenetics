package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.repositories.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel : ViewModel() {
    private val lessonRepository = LessonRepository()

    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()

    private val _userProgress = MutableStateFlow<UserProgress?>(null)
    val userProgress: StateFlow<UserProgress?> = _userProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadLesson(lessonId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Fetch lesson from Firebase
                _currentLesson.value = lessonRepository.getLessonById(lessonId)

                // In real app, load progress from database
                _userProgress.value = UserProgress(
                    lessonId = lessonId,
                    completed = false,
                    score = 0,
                    timeSpent = 0
                )
            } catch (e: Exception) {
                // Handle error - lesson will remain null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markLessonComplete() {
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                _userProgress.value = progress.copy(completed = true)
                // Save to database in real implementation
            }
        }
    }

    fun updateQuizScore(score: Int, total: Int) {
        viewModelScope.launch {
            _userProgress.value?.let { progress ->
                _userProgress.value = progress.copy(
                    score = (score.toDouble() / total * 100).toInt()
                )
            }
        }
    }
}