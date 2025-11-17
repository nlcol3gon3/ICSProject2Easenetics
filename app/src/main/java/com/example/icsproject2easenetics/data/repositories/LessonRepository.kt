// LessonRepository.kt
package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.Module
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.data.models.DifficultyLevel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LessonRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getAllModules(): List<Module> {
        return try {
            val snapshot = firestore.collection("modules")
                .orderBy("order")
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                Module(
                    moduleId = data["moduleId"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    order = (data["order"] as? Long ?: 0).toInt(),
                    icon = data["icon"] as? String ?: "",
                    totalLessons = (data["totalLessons"] as? Long ?: 0).toInt()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLessonsByModule(moduleId: String): List<Lesson> {
        return try {
            val snapshot = firestore.collection("lessons")
                .whereEqualTo("moduleId", moduleId)
                .orderBy("order")
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                Lesson(
                    lessonId = data["lessonId"] as? String ?: "",
                    moduleId = data["moduleId"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    objective = data["objective"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    content = data["content"] as? String ?: "",
                    videoUrl = data["videoUrl"] as? String,
                    duration = (data["duration"] as? Long ?: 0).toInt(),
                    difficulty = when (data["difficulty"] as? String) {
                        "BEGINNER" -> DifficultyLevel.BEGINNER
                        "INTERMEDIATE" -> DifficultyLevel.INTERMEDIATE
                        "ADVANCED" -> DifficultyLevel.ADVANCED
                        else -> DifficultyLevel.BEGINNER
                    },
                    order = (data["order"] as? Long ?: 0).toInt(),
                    hasQuiz = data["hasQuiz"] as? Boolean ?: false
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLessonById(lessonId: String): Lesson? {
        return try {
            val document = firestore.collection("lessons")
                .document(lessonId)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data ?: emptyMap<String, Any>()
                Lesson(
                    lessonId = data["lessonId"] as? String ?: "",
                    moduleId = data["moduleId"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    objective = data["objective"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    content = data["content"] as? String ?: "",
                    videoUrl = data["videoUrl"] as? String,
                    duration = (data["duration"] as? Long ?: 0).toInt(),
                    difficulty = when (data["difficulty"] as? String) {
                        "BEGINNER" -> DifficultyLevel.BEGINNER
                        "INTERMEDIATE" -> DifficultyLevel.INTERMEDIATE
                        "ADVANCED" -> DifficultyLevel.ADVANCED
                        else -> DifficultyLevel.BEGINNER
                    },
                    order = (data["order"] as? Long ?: 0).toInt(),
                    hasQuiz = data["hasQuiz"] as? Boolean ?: false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getQuizQuestions(lessonId: String): List<QuizQuestion> {
        return try {
            val snapshot = firestore.collection("quiz_questions")
                .whereEqualTo("lessonId", lessonId)
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                QuizQuestion(
                    questionId = data["questionId"] as? String ?: "",
                    question = data["question"] as? String ?: "",
                    options = (data["options"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    correctAnswer = (data["correctAnswer"] as? Long ?: 0).toInt(),
                    explanation = data["explanation"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}