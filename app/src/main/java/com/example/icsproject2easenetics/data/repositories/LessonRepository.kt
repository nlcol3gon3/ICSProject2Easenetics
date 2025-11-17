package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.Module
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LessonRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getAllModules(): List<Module> {
        return try {
            val snapshot = firestore.collection("modules")
                .orderBy("order")
                .get()
                .await()
            snapshot.toObjects(Module::class.java)
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
            snapshot.toObjects(Lesson::class.java)
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
            document.toObject(Lesson::class.java)
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
            snapshot.toObjects(QuizQuestion::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}