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

    private val _modules = MutableStateFlow(emptyList<Module>())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _moduleLessons = MutableStateFlow(emptyMap<String, List<Lesson>>())
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
                println("🔄 === MODULE VIEWMODEL DEBUG START ===")

                // 1. Fetch modules from Firebase
                println("📦 Step 1: Fetching modules from Firebase...")
                val loadedModules = lessonRepository.getAllModules()
                println("✅ Modules fetched: ${loadedModules.size}")

                loadedModules.forEach { module ->
                    println("   - Module: ${module.moduleId} | ${module.title} | Total Lessons: ${module.totalLessons}")
                }

                _modules.value = loadedModules

                // 2. Load lessons for each module
                println("📚 Step 2: Loading lessons for each module...")
                val lessonsMap = mutableMapOf<String, List<Lesson>>()

                loadedModules.forEach { module ->
                    println("   🔍 Loading lessons for module: ${module.moduleId}")
                    val lessons = lessonRepository.getLessonsByModule(module.moduleId)
                    println("   ✅ Found ${lessons.size} lessons for ${module.moduleId}")

                    lessons.forEach { lesson ->
                        println("     - Lesson: ${lesson.lessonId} | ${lesson.title} | Module: ${lesson.moduleId}")
                    }

                    lessonsMap[module.moduleId] = lessons
                }

                _moduleLessons.value = lessonsMap

                // 3. Final summary
                println("📊 === FINAL SUMMARY ===")
                println("   Total modules: ${loadedModules.size}")
                println("   Lessons map size: ${lessonsMap.size}")
                lessonsMap.forEach { (moduleId, lessons) ->
                    println("   Module $moduleId: ${lessons.size} lessons")
                }
                println("🎯 === MODULE VIEWMODEL DEBUG END ===")

            } catch (e: Exception) {
                _errorMessage.value = "Failed to load modules: ${e.message}"
                println("❌ ERROR in ModuleViewModel: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getLessonsForModule(moduleId: String): List<Lesson> {
        return _moduleLessons.value[moduleId] ?: emptyList<Lesson>().also {
            println("⚠️ No lessons found in ViewModel for module: $moduleId")
        }
    }
}