// data/repositories/ProgressRepository.kt
package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProgressRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveUserProgress(progress: UserProgress): Result<Boolean> {
        return try {
            firestore.collection("user_progress")
                .document(progress.progressId.ifBlank { generateProgressId(progress.userId, progress.lessonId) })
                .set(progress)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProgress(userId: String): List<UserProgress> {
        return try {
            val snapshot = firestore.collection("user_progress")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            snapshot.documents.map { document ->
                val data = document.data ?: emptyMap<String, Any>()
                UserProgress(
                    progressId = document.id,
                    userId = data["userId"] as? String ?: "",
                    lessonId = data["lessonId"] as? String ?: "",
                    completed = data["completed"] as? Boolean ?: false,
                    score = (data["score"] as? Long ?: 0).toInt(),
                    timeSpent = (data["timeSpent"] as? Long ?: 0).toInt(),
                    lastAccessed = (data["lastAccessed"] as? Long ?: System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProgressForLesson(userId: String, lessonId: String): UserProgress? {
        return try {
            val snapshot = firestore.collection("user_progress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("lessonId", lessonId)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { document ->
                val data = document.data ?: emptyMap<String, Any>()
                UserProgress(
                    progressId = document.id,
                    userId = data["userId"] as? String ?: "",
                    lessonId = data["lessonId"] as? String ?: "",
                    completed = data["completed"] as? Boolean ?: false,
                    score = (data["score"] as? Long ?: 0).toInt(),
                    timeSpent = (data["timeSpent"] as? Long ?: 0).toInt(),
                    lastAccessed = (data["lastAccessed"] as? Long ?: System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun markLessonComplete(userId: String, lessonId: String, score: Int = 0, timeSpent: Int = 0): Result<Boolean> {
        return try {
            val progressId = generateProgressId(userId, lessonId)
            val progress = UserProgress(
                progressId = progressId,
                userId = userId,
                lessonId = lessonId,
                completed = true,
                score = score,
                timeSpent = timeSpent,
                lastAccessed = System.currentTimeMillis()
            )

            firestore.collection("user_progress")
                .document(progressId)
                .set(progress)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateQuizScore(userId: String, lessonId: String, score: Int): Result<Boolean> {
        return try {
            val progressId = generateProgressId(userId, lessonId)

            firestore.collection("user_progress")
                .document(progressId)
                .update(
                    mapOf(
                        "score" to score,
                        "lastAccessed" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateProgressId(userId: String, lessonId: String): String {
        return "${userId}_$lessonId"
    }
}