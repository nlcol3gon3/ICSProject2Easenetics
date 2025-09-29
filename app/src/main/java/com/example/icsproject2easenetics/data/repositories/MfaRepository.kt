package com.example.icsproject2easenetics.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
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

    fun startPhoneNumberVerification(
        phoneNumber: String,
        timeout: Long = 60L
    ) = callbackFlow<PhoneVerificationState> {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthProvider.PhoneAuthCredential) {
                trySend(PhoneVerificationState.Completed(credential))
                close()
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                trySend(PhoneVerificationState.Failed(exception))
                close()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(PhoneVerificationState.CodeSent(verificationId, token))
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timeout, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose { }
    }

    suspend fun verifyPhoneNumber(
        verificationId: String,
        code: String
    ): Result<Boolean> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            firebaseAuth.signInWithCredential(credential).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
            // Note: This requires re-authentication in production
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

sealed class PhoneVerificationState {
    object Started : PhoneVerificationState()
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : PhoneVerificationState()
    data class Completed(val credential: PhoneAuthProvider.PhoneAuthCredential) : PhoneVerificationState()
    data class Failed(val exception: Exception) : PhoneVerificationState()
}