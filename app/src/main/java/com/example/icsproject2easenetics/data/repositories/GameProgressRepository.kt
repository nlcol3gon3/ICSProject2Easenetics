// Create new file: GameProgressRepository.kt
package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.UserGameProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameProgressRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val COLLECTION_GAME_PROGRESS = "gameProgress"
    }

    suspend fun getUserGameProgress(gameId: String): UserGameProgress? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val document = db.collection(COLLECTION_GAME_PROGRESS)
                .document("${userId}_$gameId")
                .get()
                .await()

            if (document.exists()) {
                document.toObject(UserGameProgress::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun saveUserGameProgress(progress: UserGameProgress) {
        val userId = auth.currentUser?.uid ?: return
        try {
            db.collection(COLLECTION_GAME_PROGRESS)
                .document("${userId}_${progress.gameId}")
                .set(progress.toMap())
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateCompletedLevel(gameId: String, level: Int, score: Int) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val progress = getUserGameProgress(gameId) ?: UserGameProgress(
                userId = userId,
                gameId = gameId,
                completedLevels = emptyList(),
                bestScores = emptyMap()
            )

            val updatedCompletedLevels = if (level !in progress.completedLevels) {
                progress.completedLevels + level
            } else {
                progress.completedLevels
            }

            val updatedBestScores = progress.bestScores.toMutableMap().apply {
                val currentBest = this[level] ?: 0
                if (score > currentBest) {
                    this[level] = score
                }
            }

            val updatedProgress = progress.copy(
                completedLevels = updatedCompletedLevels,
                bestScores = updatedBestScores,
                totalGamesPlayed = progress.totalGamesPlayed + 1,
                lastPlayed = System.currentTimeMillis()
            )

            saveUserGameProgress(updatedProgress)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}