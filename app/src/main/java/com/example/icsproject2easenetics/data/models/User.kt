package com.example.icsproject2easenetics.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val userId: String,
    val name: String,
    val email: String,
    val createdAt: Date,
    var lastLogin: Date,
    var accessibilitySettings: AccessibilitySettings = AccessibilitySettings()
)

data class AccessibilitySettings(
    val textSize: TextSize = TextSize.LARGE,
    val highContrast: Boolean = true,
    val voiceNarration: Boolean = true,
    val reducedMotion: Boolean = false
)

enum class TextSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}