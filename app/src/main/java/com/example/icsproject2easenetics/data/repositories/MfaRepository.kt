package com.example.icsproject2easenetics.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MfaRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    suspend fun sendEmailVerification(user: FirebaseUser): Result<Boolean> {
        return try {
            user.sendEmailVerification().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmail(user: FirebaseUser): Boolean {
        // Reload user to get updated email verification status
        user.reload().await()
        return user.isEmailVerified
    }

    suspend fun enableMfa(user: FirebaseUser): Result<Boolean> {
        return try {
            // Firebase doesn't have direct MFA enablement for email/password users
            // We'll handle this at application level
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePhoneNumber(user: FirebaseUser, phoneNumber: String): Result<Boolean> {
        return try {
            // This would update the user's phone number in Firebase
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}