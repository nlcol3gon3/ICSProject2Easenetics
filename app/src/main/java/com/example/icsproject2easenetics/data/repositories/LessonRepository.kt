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
            println("🔍 LessonRepository: Fetching modules from Firestore...")
            val snapshot = firestore.collection("modules")
                .orderBy("order")
                .get()
                .await()

            println("📄 Firestore returned ${snapshot.documents.size} module documents")

            val modules = snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                val module = Module(
                    moduleId = data["moduleId"] as? String ?: "",
                    title = data["title"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    order = (data["order"] as? Long ?: 0).toInt(),
                    icon = data["icon"] as? String ?: "",
                    totalLessons = (data["totalLessons"] as? Long ?: 0).toInt()
                )
                println("   📝 Parsed module: ${module.moduleId} - ${module.title}")
                module
            }
            println("✅ LessonRepository: Successfully parsed ${modules.size} modules")
            modules
        } catch (e: Exception) {
            println("❌ LessonRepository: Error fetching modules: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getLessonsByModule(moduleId: String): List<Lesson> {
        return try {
            println("🔍 LessonRepository: Fetching lessons for module: $moduleId")

            val snapshot = firestore.collection("lessons")
                .whereEqualTo("moduleId", moduleId)
                .orderBy("order")
                .get()
                .await()

            println("📄 Firestore returned ${snapshot.documents.size} lesson documents for module $moduleId")

            val lessons = snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                val lesson = Lesson(
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
                println("   📝 Parsed lesson: ${lesson.lessonId} - ${lesson.title} (Module: ${lesson.moduleId})")
                lesson
            }
            println("✅ LessonRepository: Successfully parsed ${lessons.size} lessons for module $moduleId")
            lessons
        } catch (e: Exception) {
            println("❌ LessonRepository: Error fetching lessons for module $moduleId: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    // Add this method to fix the LessonViewModel error
    suspend fun getLessonById(lessonId: String): Lesson? {
        return try {
            println("🔍 LessonRepository: Fetching lesson by ID: $lessonId")
            val document = firestore.collection("lessons")
                .document(lessonId)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data ?: emptyMap<String, Any>()
                val lesson = Lesson(
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
                println("✅ LessonRepository: Found lesson: ${lesson.title}")
                lesson
            } else {
                println("❌ LessonRepository: Lesson $lessonId not found in Firestore")
                null
            }
        } catch (e: Exception) {
            println("❌ LessonRepository: Error fetching lesson $lessonId: ${e.message}")
            null
        }
    }

    suspend fun getQuizQuestions(lessonId: String): List<QuizQuestion> {
        return try {
            println("🔍 LessonRepository: Fetching quiz questions for lesson: $lessonId")

            val snapshot = firestore.collection("quiz_questions")
                .whereEqualTo("lessonId", lessonId)
                .get()
                .await()

            println("📄 Firestore returned ${snapshot.documents.size} quiz questions for lesson $lessonId")

            val questions = snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                val question = QuizQuestion(
                    questionId = data["questionId"] as? String ?: "",
                    question = data["question"] as? String ?: "",
                    options = (data["options"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                    correctAnswer = (data["correctAnswer"] as? Long ?: 0).toInt(),
                    explanation = data["explanation"] as? String ?: ""
                )
                println("   📝 Parsed question: ${question.questionId} - ${question.question.take(50)}...")
                question
            }
            println("✅ LessonRepository: Successfully parsed ${questions.size} quiz questions for lesson $lessonId")
            questions
        } catch (e: Exception) {
            println("❌ LessonRepository: Error fetching quiz questions for lesson $lessonId: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}