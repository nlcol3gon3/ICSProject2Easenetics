package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.AccessibilitySettings
import com.example.icsproject2easenetics.data.models.ButtonSize
import com.example.icsproject2easenetics.data.models.ColorBlindMode
import com.example.icsproject2easenetics.data.models.TextSize
import com.example.icsproject2easenetics.data.models.TouchDelay
import com.example.icsproject2easenetics.data.repositories.UserRepository
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccessibilityViewModel : ViewModel() {
    private val userRepository = UserRepository(FirebaseFirestore.getInstance())

    private val _accessibilitySettings = MutableStateFlow(AccessibilityManager.currentSettings)
    val accessibilitySettings: StateFlow<AccessibilitySettings> = _accessibilitySettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUserSettings()
    }

    private fun loadUserSettings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser
                currentUser?.uid?.let { userId ->
                    val user = userRepository.getUser(userId)
                    user?.accessibilitySettings?.let { settings ->
                        // Update both local state and global manager
                        _accessibilitySettings.value = settings
                        AccessibilityManager.updateSettings(settings)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load accessibility settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSettings() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val currentUser = Firebase.auth.currentUser
                currentUser?.uid?.let { userId ->
                    val user = userRepository.getUser(userId)
                    user?.let {
                        val updatedUser = it.copy(accessibilitySettings = _accessibilitySettings.value)
                        userRepository.updateUser(updatedUser)

                        // Update global manager
                        AccessibilityManager.updateSettings(_accessibilitySettings.value)

                        _saveSuccess.value = true
                    } ?: run {
                        _errorMessage.value = "User profile not found"
                    }
                } ?: run {
                    _errorMessage.value = "User not logged in"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save settings: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Text Size Settings
    fun setTextSize(textSize: TextSize) {
        updateSettings { it.copy(textSize = textSize) }
    }

    // Visual Settings
    fun setHighContrast(enabled: Boolean) {
        updateSettings { it.copy(highContrast = enabled) }
    }

    fun setColorBlindMode(mode: ColorBlindMode) {
        updateSettings { it.copy(colorBlindMode = mode) }
    }

    fun setSimplifiedLayout(enabled: Boolean) {
        updateSettings { it.copy(simplifiedLayout = enabled) }
    }

    // Audio Settings
    fun setVoiceNarration(enabled: Boolean) {
        updateSettings { it.copy(voiceNarration = enabled) }
    }

    fun setAudioDescription(enabled: Boolean) {
        updateSettings { it.copy(audioDescription = enabled) }
    }

    fun setVisualAlerts(enabled: Boolean) {
        updateSettings { it.copy(visualAlerts = enabled) }
    }

    // Interaction Settings
    fun setScreenReader(enabled: Boolean) {
        updateSettings { it.copy(screenReader = enabled) }
    }

    fun setButtonSize(buttonSize: ButtonSize) {
        updateSettings { it.copy(buttonSize = buttonSize) }
    }

    fun setTouchDelay(touchDelay: TouchDelay) {
        updateSettings { it.copy(touchDelay = touchDelay) }
    }

    fun setReducedMotion(enabled: Boolean) {
        updateSettings { it.copy(reducedMotion = enabled) }
    }

    // Preset profiles
    fun setVisionImpairedProfile() {
        updateSettings {
            it.copy(
                textSize = TextSize.EXTRA_LARGE,
                highContrast = true,
                buttonSize = ButtonSize.LARGE,
                touchDelay = TouchDelay.LONG,
                screenReader = true,
                simplifiedLayout = true
            )
        }
        saveSettings()
    }

    fun setHearingImpairedProfile() {
        updateSettings {
            it.copy(
                visualAlerts = true,
                audioDescription = true,
                voiceNarration = false,
                highContrast = true
            )
        }
        saveSettings()
    }

    fun setMotorImpairedProfile() {
        updateSettings {
            it.copy(
                buttonSize = ButtonSize.LARGE,
                touchDelay = TouchDelay.LONG,
                simplifiedLayout = true,
                textSize = TextSize.LARGE
            )
        }
        saveSettings()
    }

    fun resetToDefaults() {
        val defaultSettings = AccessibilitySettings()
        _accessibilitySettings.value = defaultSettings
        AccessibilityManager.updateSettings(defaultSettings)
        saveSettings()
    }

    private fun updateSettings(update: (AccessibilitySettings) -> AccessibilitySettings) {
        val newSettings = update(_accessibilitySettings.value)
        _accessibilitySettings.value = newSettings
        _saveSuccess.value = false
        _errorMessage.value = null
    }

    fun clearSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}