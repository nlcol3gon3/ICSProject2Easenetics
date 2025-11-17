// ui/viewmodels/ProfileViewModel.kt
package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.User
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository(FirebaseFirestore.getInstance())

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadUserProfile(userId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId)
                _userProfile.value = user
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMfaSetting(userId: String, mfaEnabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUser(userId)
                currentUser?.let { user ->
                    val updatedUser = user.copy(mfaEnabled = mfaEnabled)
                    userRepository.updateUser(updatedUser)
                    _userProfile.value = updatedUser
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update MFA settings: ${e.message}"
            }
        }
    }

    fun setupEmailMfa(userId: String) {
        viewModelScope.launch {
            try {
                val currentUser = userRepository.getUser(userId)
                currentUser?.let { user ->
                    val updatedUser = user.copy(preferredMfaMethod = com.example.icsproject2easenetics.data.models.MfaMethod.EMAIL)
                    userRepository.updateUser(updatedUser)
                    _userProfile.value = updatedUser
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to setup email MFA: ${e.message}"
            }
        }
    }
}