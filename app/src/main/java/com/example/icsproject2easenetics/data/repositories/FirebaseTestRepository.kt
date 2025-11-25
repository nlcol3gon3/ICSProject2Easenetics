package com.example.icsproject2easenetics.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTestRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun testFirebaseConnection(): Boolean {
        return try {
            // Test Firestore connection by writing a test document
            val testData = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "test" to "Firebase connection test",
                "app" to "Easenetics"
            )

            firestore.collection("connection_tests")
                .document("android_app")
                .set(testData)
                .await()


            auth.app.name

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun testFirebaseRead(): Boolean {
        return try {
            // Test reading from Firestore
            val document = firestore.collection("connection_tests")
                .document("android_app")
                .get()
                .await()

            document.exists()
        } catch (e: Exception) {
            false
        }
    }
}