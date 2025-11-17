package com.example.icsproject2easenetics.data.models

data class AccessibilitySettings(
    val textSize: TextSize = TextSize.MEDIUM,
    val highContrast: Boolean = false,
    val voiceNarration: Boolean = true,
    val reducedMotion: Boolean = false,
    val visualAlerts: Boolean = true,
    val screenReader: Boolean = false,
    val buttonSize: ButtonSize = ButtonSize.MEDIUM,
    val colorBlindMode: ColorBlindMode = ColorBlindMode.NONE,
    val touchDelay: TouchDelay = TouchDelay.NORMAL,
    val audioDescription: Boolean = false,
    val simplifiedLayout: Boolean = false
) {
    constructor() : this(TextSize.MEDIUM, false, true, false, true, false, ButtonSize.MEDIUM, ColorBlindMode.NONE, TouchDelay.NORMAL, false, false)
}

enum class TextSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}

enum class ButtonSize {
    SMALL, MEDIUM, LARGE
}

enum class ColorBlindMode {
    NONE, PROTANOPIA, DEUTERANOPIA, TRITANOPIA
}

enum class TouchDelay {
    NONE, SHORT, NORMAL, LONG
}