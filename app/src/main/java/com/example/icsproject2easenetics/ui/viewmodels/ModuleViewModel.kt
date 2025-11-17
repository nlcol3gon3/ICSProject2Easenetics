package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.Module
import com.example.icsproject2easenetics.data.repositories.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModuleViewModel : ViewModel() {
    private val lessonRepository = LessonRepository()

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _moduleLessons = MutableStateFlow<Map<String, List<Lesson>>>(emptyMap())
    val moduleLessons: StateFlow<Map<String, List<Lesson>>> = _moduleLessons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadAllModules() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // Fetch modules from Firebase
                val modules = lessonRepository.getAllModules()
                _modules.value = modules

                // Load lessons for each module
                val lessonsMap = mutableMapOf<String, List<Lesson>>()
                modules.forEach { module ->
                    val lessons = lessonRepository.getLessonsByModule(module.moduleId)
                    lessonsMap[module.moduleId] = lessons
                }
                _moduleLessons.value = lessonsMap

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load modules: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getLessonsForModule(moduleId: String): List<Lesson> {
        return _moduleLessons.value[moduleId] ?: emptyList()
    }
}