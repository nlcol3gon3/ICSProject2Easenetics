package com.example.icsproject2easenetics.data.models

import java.util.Date

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Date = Date(),
    var lastLogin: Date = Date(),
    var accessibilitySettings: AccessibilitySettings = AccessibilitySettings()
) {
    constructor() : this("", "", "", Date(), Date(), AccessibilitySettings())
}

data class AccessibilitySettings(
    val textSize: TextSize = TextSize.LARGE,
    val highContrast: Boolean = true,
    val voiceNarration: Boolean = true,
    val reducedMotion: Boolean = false
) {
    constructor() : this(TextSize.LARGE, true, true, false)
}

enum class TextSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}