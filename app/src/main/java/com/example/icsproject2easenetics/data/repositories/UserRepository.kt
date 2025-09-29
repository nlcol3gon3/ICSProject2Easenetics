package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepository(private val firestore: FirebaseFirestore) {

    suspend fun createUser(user: User) {
        try {
            firestore.collection("users")
                .document(user.userId)
                .set(user)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateUser(user: User) {
        try {
            firestore.collection("users")
                .document(user.userId)
                .set(user)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }
}