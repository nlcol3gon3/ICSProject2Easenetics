package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.MfaVerification
import com.example.icsproject2easenetics.data.models.User
import com.example.icsproject2easenetics.data.repositories.AuthRepository
import com.example.icsproject2easenetics.data.repositories.MfaRepository
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.example.icsproject2easenetics.service.TotpService
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
    private val mfaRepository = MfaRepository(FirebaseAuth.getInstance())
    private val totpService = TotpService()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // MFA State Flows
    private val _mfaSetupState = MutableStateFlow<MfaSetupState>(MfaSetupState.Idle)
    val mfaSetupState: StateFlow<MfaSetupState> = _mfaSetupState.asStateFlow()

    private val _mfaVerificationState = MutableStateFlow<MfaVerificationState>(MfaVerificationState.Idle)
    val mfaVerificationState: StateFlow<MfaVerificationState> = _mfaVerificationState.asStateFlow()

    init {
        _currentUser.value = authRepository.currentUser
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _mfaVerificationState.value = MfaVerificationState.Idle

        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                _currentUser.value = result

                // Check if MFA is required
                result?.let { user ->
                    val userProfile = userRepository.getUser(user.uid)
                    if (userProfile?.mfaEnabled == true) {
                        _mfaVerificationState.value = MfaVerificationState.Required(userProfile.preferredMfaMethod)
                    } else {
                        updateUserLastLogin(user.uid)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyMfa(verification: MfaVerification) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val user = _currentUser.value
                val userProfile = user?.let { userRepository.getUser(it.uid) }

                when (verification.method) {
                    com.example.icsproject2easenetics.data.models.MfaMethod.EMAIL -> {
                        // Verify email code (would be sent via email)
                        val isValid = verifyEmailCode(verification.code)
                        if (isValid) {
                            completeMfaVerification()
                        } else {
                            _errorMessage.value = "Invalid verification code"
                            _mfaVerificationState.value = MfaVerificationState.Error("Invalid verification code")
                        }
                    }
                    com.example.icsproject2easenetics.data.models.MfaMethod.PHONE -> {
                        // Verify SMS code
                        // This would use the verification ID from phone setup
                        _errorMessage.value = "Phone verification not yet implemented"
                        _mfaVerificationState.value = MfaVerificationState.Error("Phone verification not yet implemented")
                    }
                    com.example.icsproject2easenetics.data.models.MfaMethod.AUTHENTICATOR_APP -> {
                        // Verify TOTP code
                        val isValid = userProfile?.totpSecret?.let { secret ->
                            totpService.verifyTotpCode(secret, verification.code)
                        } ?: false

                        if (isValid) {
                            completeMfaVerification()
                        } else {
                            _errorMessage.value = "Invalid authenticator code"
                            _mfaVerificationState.value = MfaVerificationState.Error("Invalid authenticator code")
                        }
                    }
                    else -> {
                        _errorMessage.value = "Unsupported MFA method"
                        _mfaVerificationState.value = MfaVerificationState.Error("Unsupported MFA method")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "MFA verification failed: ${e.message}"
                _mfaVerificationState.value = MfaVerificationState.Error("MFA verification failed: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun completeMfaVerification() {
        _currentUser.value?.let { user ->
            updateUserLastLogin(user.uid)
            _mfaVerificationState.value = MfaVerificationState.Verified
        }
    }

    private fun verifyEmailCode(code: String): Boolean {
        // In a real implementation, you'd verify against a code sent via email
        // For now, we'll use a simple demo verification
        return code.length == 6 && code.all { it.isDigit() }
    }

    // Setup MFA methods
    fun setupEmailMfa(userId: String) {
        viewModelScope.launch {
            try {
                _mfaSetupState.value = MfaSetupState.Loading
                val user = _currentUser.value
                if (user != null) {
                    val result = mfaRepository.sendEmailVerification(user)
                    if (result.isSuccess) {
                        _mfaSetupState.value = MfaSetupState.EmailSent
                        // Update user profile
                        updateUserMfaMethod(userId, com.example.icsproject2easenetics.data.models.MfaMethod.EMAIL)
                    } else {
                        _mfaSetupState.value = MfaSetupState.Error("Failed to send verification email")
                    }
                }
            } catch (e: Exception) {
                _mfaSetupState.value = MfaSetupState.Error("Email MFA setup failed: ${e.message}")
            }
        }
    }

    fun setupAuthenticatorMfa(userId: String) {
        viewModelScope.launch {
            try {
                _mfaSetupState.value = MfaSetupState.Loading

                val secret = totpService.generateSecret()
                val user = _currentUser.value
                val email = user?.email ?: ""

                val totpUri = totpService.generateTotpUri(secret, email)

                // Update user with TOTP secret
                val currentUserProfile = userRepository.getUser(userId)
                currentUserProfile?.let { profile ->
                    val updatedUser = profile.copy(
                        totpSecret = secret,
                        preferredMfaMethod = com.example.icsproject2easenetics.data.models.MfaMethod.AUTHENTICATOR_APP
                    )
                    userRepository.updateUser(updatedUser)
                }

                _mfaSetupState.value = MfaSetupState.AuthenticatorReady(secret, totpUri)
            } catch (e: Exception) {
                _mfaSetupState.value = MfaSetupState.Error("Authenticator setup failed: ${e.message}")
            }
        }
    }

    fun verifyAuthenticatorSetup(userId: String, code: String) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getUser(userId)
                val secret = userProfile?.totpSecret

                if (secret != null && totpService.verifyTotpCode(secret, code)) {
                    // Enable MFA for the user
                    userProfile.let { profile ->
                        val updatedUser = profile.copy(
                            mfaEnabled = true,
                            preferredMfaMethod = com.example.icsproject2easenetics.data.models.MfaMethod.AUTHENTICATOR_APP
                        )
                        userRepository.updateUser(updatedUser)
                    }
                    _mfaSetupState.value = MfaSetupState.Completed
                } else {
                    _mfaSetupState.value = MfaSetupState.Error("Invalid verification code")
                }
            } catch (e: Exception) {
                _mfaSetupState.value = MfaSetupState.Error("Verification failed: ${e.message}")
            }
        }
    }

    private suspend fun updateUserMfaMethod(userId: String, method: com.example.icsproject2easenetics.data.models.MfaMethod) {
        val currentUser = userRepository.getUser(userId)
        currentUser?.let { user ->
            val updatedUser = user.copy(
                mfaEnabled = true,
                preferredMfaMethod = method
            )
            userRepository.updateUser(updatedUser)
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
            _mfaVerificationState.value = MfaVerificationState.Idle
            _mfaSetupState.value = MfaSetupState.Idle
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearMfaState() {
        _mfaSetupState.value = MfaSetupState.Idle
        _mfaVerificationState.value = MfaVerificationState.Idle
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

// MFA State sealed classes
sealed class MfaSetupState {
    object Idle : MfaSetupState()
    object Loading : MfaSetupState()
    object EmailSent : MfaSetupState()
    data class AuthenticatorReady(val secret: String, val totpUri: String) : MfaSetupState()
    object Completed : MfaSetupState()
    data class Error(val message: String) : MfaSetupState()
}

sealed class MfaVerificationState {
    object Idle : MfaVerificationState()
    data class Required(val method: com.example.icsproject2easenetics.data.models.MfaMethod) : MfaVerificationState()
    object Verified : MfaVerificationState()
    data class Error(val message: String) : MfaVerificationState()
}