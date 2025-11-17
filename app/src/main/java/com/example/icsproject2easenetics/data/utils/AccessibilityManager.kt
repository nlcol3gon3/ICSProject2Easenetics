package com.example.icsproject2easenetics.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.data.models.AccessibilitySettings
import com.example.icsproject2easenetics.data.models.ButtonSize
import com.example.icsproject2easenetics.data.models.TextSize

object AccessibilityManager {
    var currentSettings by mutableStateOf(AccessibilitySettings())
        private set

    fun updateSettings(newSettings: AccessibilitySettings) {
        currentSettings = newSettings
    }

    // Text size scaling
    @Composable
    fun getScaledTitleLarge() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontSize = 26.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp)
    }

    @Composable
    fun getScaledTitleMedium() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp)
    }

    @Composable
    fun getScaledTitleSmall() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(fontSize = 18.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.titleSmall.copy(fontSize = 20.sp)
    }

    @Composable
    fun getScaledBodyLarge() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
    }

    @Composable
    fun getScaledBodyMedium() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp)
    }

    @Composable
    fun getScaledBodySmall() = when (currentSettings.textSize) {
        TextSize.SMALL -> androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
        TextSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
        TextSize.LARGE -> androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
        TextSize.EXTRA_LARGE -> androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)
    }

    // Button size scaling
    fun getButtonHeight() = when (currentSettings.buttonSize) {
        ButtonSize.SMALL -> 40.dp
        ButtonSize.MEDIUM -> 56.dp
        ButtonSize.LARGE -> 64.dp
    }

    fun getButtonMinWidth() = when (currentSettings.buttonSize) {
        ButtonSize.SMALL -> 64.dp
        ButtonSize.MEDIUM -> 80.dp
        ButtonSize.LARGE -> 96.dp
    }
}

// CompositionLocal for accessing accessibility settings
val LocalAccessibilitySettings = staticCompositionLocalOf { AccessibilityManager.currentSettings }