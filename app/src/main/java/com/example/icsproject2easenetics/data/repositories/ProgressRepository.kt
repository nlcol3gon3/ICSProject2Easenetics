package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProgressRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveUserProgress(progress: UserProgress): Result<Boolean> {
        return try {
            val progressId = progress.progressId.ifBlank { generateProgressId(progress.userId, progress.lessonId) }
            println("🔄 ProgressRepository: Saving progress - ID: $progressId, User: ${progress.userId}, Lesson: ${progress.lessonId}, Completed: ${progress.completed}, Score: ${progress.score}")

            firestore.collection("user_progress")
                .document(progressId)
                .set(progress)
                .await()

            println("✅ ProgressRepository: Progress saved successfully")
            Result.success(true)
        } catch (e: Exception) {
            println("❌ ProgressRepository: Error saving progress: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getUserProgress(userId: String): List<UserProgress> {
        return try {
            println("🔄 ProgressRepository: Fetching progress for user: $userId")

            val snapshot = firestore.collection("user_progress")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            println("📄 ProgressRepository: Found ${snapshot.documents.size} progress records")

            val progressList = snapshot.documents.map { document ->
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

            println("✅ ProgressRepository: Successfully parsed ${progressList.size} progress records")
            progressList
        } catch (e: Exception) {
            println("❌ ProgressRepository: Error fetching progress: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getProgressForLesson(userId: String, lessonId: String): UserProgress? {
        return try {
            println("🔄 ProgressRepository: Fetching progress for user: $userId, lesson: $lessonId")

            val snapshot = firestore.collection("user_progress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("lessonId", lessonId)
                .get()
                .await()

            val document = snapshot.documents.firstOrNull()
            document?.let { doc ->
                val data = doc.data ?: emptyMap<String, Any>()
                UserProgress(
                    progressId = doc.id,
                    userId = data["userId"] as? String ?: "",
                    lessonId = data["lessonId"] as? String ?: "",
                    completed = data["completed"] as? Boolean ?: false,
                    score = (data["score"] as? Long ?: 0).toInt(),
                    timeSpent = (data["timeSpent"] as? Long ?: 0).toInt(),
                    lastAccessed = (data["lastAccessed"] as? Long ?: System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            println("❌ ProgressRepository: Error fetching lesson progress: ${e.message}")
            null
        }
    }

    suspend fun markLessonComplete(userId: String, lessonId: String, score: Int = 0, timeSpent: Int = 0): Result<Boolean> {
        return try {
            val progressId = generateProgressId(userId, lessonId)
            println("🔄 ProgressRepository: Marking lesson complete - ID: $progressId, User: $userId, Lesson: $lessonId, Score: $score")

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

            println("✅ ProgressRepository: Lesson marked as complete successfully")
            Result.success(true)
        } catch (e: Exception) {
            println("❌ ProgressRepository: Error marking lesson complete: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun updateQuizScore(userId: String, lessonId: String, score: Int): Result<Boolean> {
        return try {
            val progressId = generateProgressId(userId, lessonId)
            println("🔄 ProgressRepository: Updating quiz score - ID: $progressId, User: $userId, Lesson: $lessonId, Score: $score")

            val existingProgress = getProgressForLesson(userId, lessonId)

            val progress = UserProgress(
                progressId = progressId,
                userId = userId,
                lessonId = lessonId,
                completed = existingProgress?.completed ?: (score >= 70),
                score = score,
                timeSpent = existingProgress?.timeSpent ?: 0,
                lastAccessed = System.currentTimeMillis()
            )

            firestore.collection("user_progress")
                .document(progressId)
                .set(progress)
                .await()

            println("✅ ProgressRepository: Quiz score updated successfully")
            Result.success(true)
        } catch (e: Exception) {
            println("❌ ProgressRepository: Error updating quiz score: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getCompletedLessonsCount(userId: String): Int {
        return try {
            val snapshot = firestore.collection("user_progress")
                .whereEqualTo("userId", userId)
                .whereEqualTo("completed", true)
                .get()
                .await()
            snapshot.documents.size
        } catch (e: Exception) {
            println("⚠️ ProgressRepository: Using fallback for completed lessons count: ${e.message}")
            // Fallback: count from local progress list
            getUserProgress(userId).count { it.completed }
        }
    }

    suspend fun getTotalQuizScore(userId: String): Int {
        return try {
            // Use a simpler query that doesn't require composite index
            val userProgress = getUserProgress(userId)
            userProgress.sumOf { it.score }
        } catch (e: Exception) {
            println("⚠️ ProgressRepository: Using fallback for total quiz score: ${e.message}")
            0
        }
    }

    suspend fun getQuizzesTakenCount(userId: String): Int {
        return try {
            // Use a simpler query that doesn't require composite index
            val userProgress = getUserProgress(userId)
            userProgress.count { it.score > 0 }
        } catch (e: Exception) {
            println("⚠️ ProgressRepository: Using fallback for quizzes taken count: ${e.message}")
            0
        }
    }

    suspend fun getTotalLearningTime(userId: String): Int {
        return try {
            // Use a simpler query that doesn't require composite index
            val userProgress = getUserProgress(userId)
            userProgress.sumOf { it.timeSpent }
        } catch (e: Exception) {
            println("⚠️ ProgressRepository: Using fallback for learning time: ${e.message}")
            0
        }
    }

    private fun generateProgressId(userId: String, lessonId: String): String {
        return "${userId}_$lessonId"
    }
}