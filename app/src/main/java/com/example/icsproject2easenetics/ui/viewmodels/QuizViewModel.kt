package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.data.repositories.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val lessonRepository = LessonRepository()

    private val _questions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val questions: StateFlow<List<QuizQuestion>> = _questions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadQuizQuestions(lessonId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val quizQuestions = lessonRepository.getQuizQuestions(lessonId)
                _questions.value = quizQuestions
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load quiz questions: ${e.message}"
                _questions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearQuestions() {
        _questions.value = emptyList()
        _errorMessage.value = null
    }
}