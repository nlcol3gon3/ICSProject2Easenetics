package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.AccessibilitySettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccessibilityViewModel : ViewModel() {
    private val _accessibilitySettings = MutableStateFlow(
        AccessibilitySettings(
            textSize = com.example.icsproject2easenetics.data.models.TextSize.LARGE,
            highContrast = true,
            voiceNarration = true,
            reducedMotion = false,
            visualAlerts = true
        )
    )
    val accessibilitySettings: StateFlow<AccessibilitySettings> = _accessibilitySettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun increaseTextSize() {
        viewModelScope.launch {
            val current = _accessibilitySettings.value
            val newSize = when (current.textSize) {
                com.example.icsproject2easenetics.data.models.TextSize.SMALL ->
                    com.example.icsproject2easenetics.data.models.TextSize.MEDIUM
                com.example.icsproject2easenetics.data.models.TextSize.MEDIUM ->
                    com.example.icsproject2easenetics.data.models.TextSize.LARGE
                com.example.icsproject2easenetics.data.models.TextSize.LARGE ->
                    com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE
                com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE ->
                    com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE
                else -> com.example.icsproject2easenetics.data.models.TextSize.LARGE
            }
            _accessibilitySettings.value = current.copy(textSize = newSize)
        }
    }

    fun decreaseTextSize() {
        viewModelScope.launch {
            val current = _accessibilitySettings.value
            val newSize = when (current.textSize) {
                com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE ->
                    com.example.icsproject2easenetics.data.models.TextSize.LARGE
                com.example.icsproject2easenetics.data.models.TextSize.LARGE ->
                    com.example.icsproject2easenetics.data.models.TextSize.MEDIUM
                com.example.icsproject2easenetics.data.models.TextSize.MEDIUM ->
                    com.example.icsproject2easenetics.data.models.TextSize.SMALL
                com.example.icsproject2easenetics.data.models.TextSize.SMALL ->
                    com.example.icsproject2easenetics.data.models.TextSize.SMALL
                else -> com.example.icsproject2easenetics.data.models.TextSize.LARGE
            }
            _accessibilitySettings.value = current.copy(textSize = newSize)
        }
    }

    fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch {
            val current = _accessibilitySettings.value
            _accessibilitySettings.value = current.copy(highContrast = enabled)
        }
    }

    fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch {
            val current = _accessibilitySettings.value
            _accessibilitySettings.value = current.copy(reducedMotion = enabled)
        }
    }

    fun setVisualAlerts(enabled: Boolean) {
        viewModelScope.launch {
            val current = _accessibilitySettings.value
            _accessibilitySettings.value = current.copy(visualAlerts = enabled)
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _accessibilitySettings.value = AccessibilitySettings(
                textSize = com.example.icsproject2easenetics.data.models.TextSize.LARGE,
                highContrast = true,
                voiceNarration = true,
                reducedMotion = false,
                visualAlerts = true
            )
        }
    }
}