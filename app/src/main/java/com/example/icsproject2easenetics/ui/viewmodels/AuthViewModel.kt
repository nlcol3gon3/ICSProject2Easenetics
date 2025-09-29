package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.User
import com.example.icsproject2easenetics.data.repositories.AuthRepository
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository(FirebaseAuth.getInstance())
    private val userRepository = UserRepository(FirebaseFirestore.getInstance())

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        _currentUser.value = authRepository.currentUser
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                _currentUser.value = result
                // Update user's last login in Firestore
                result?.let { user ->
                    updateUserLastLogin(user.uid)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val result = authRepository.register(name, email, password)
                _currentUser.value = result
                // Create user document in Firestore
                result?.let { firebaseUser ->
                    val user = User(
                        userId = firebaseUser.uid,
                        name = name,
                        email = email,
                        createdAt = Date(),
                        lastLogin = Date()
                    )
                    userRepository.createUser(user)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _currentUser.value = null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private suspend fun updateUserLastLogin(userId: String) {
        try {
            val user = userRepository.getUser(userId)
            user?.let {
                val updatedUser = it.copy(lastLogin = Date())
                userRepository.updateUser(updatedUser)
            }
        } catch (e: Exception) {
            // Log error but don't show to user as this is a background operation
            println("Failed to update last login: ${e.message}")
        }
    }
}