package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.service.VoiceService
import com.example.icsproject2easenetics.ui.viewmodels.AccessibilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessibilityScreen(
    onBack: () -> Unit,
    viewModel: AccessibilityViewModel = viewModel()
) {
    val context = LocalContext.current
    val voiceService = remember { VoiceService(context) }

    val accessibilitySettings by viewModel.accessibilitySettings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Show success message
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            // Auto-hide success message after 3 seconds
            kotlinx.coroutines.delay(3000)
            viewModel.clearSaveSuccess()
        }
    }

    // Clear error after some time
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            kotlinx.coroutines.delay(5000)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Accessibility Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success message
            if (saveSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "✅ Settings saved successfully!",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error message
            if (!errorMessage.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "❌ $errorMessage",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Quick Presets Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Accessibility, "Presets", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "Quick Presets",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Apply preset configurations for common needs",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setVisionImpairedProfile() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("👁️ Vision")
                        }
                        Button(
                            onClick = { viewModel.setHearingImpairedProfile() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("👂 Hearing")
                        }
                        Button(
                            onClick = { viewModel.setMotorImpairedProfile() },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("🖐️ Motor")
                        }
                    }
                }
            }

            // Text & Display Settings
            TextDisplaySettings(
                settings = accessibilitySettings,
                onTextSizeChange = viewModel::setTextSize,
                onHighContrastChange = viewModel::setHighContrast,
                onColorBlindModeChange = viewModel::setColorBlindMode,
                onSimplifiedLayoutChange = viewModel::setSimplifiedLayout
            )

            // Audio & Voice Settings
            AudioVoiceSettings(
                settings = accessibilitySettings,
                onVoiceNarrationChange = viewModel::setVoiceNarration,
                onAudioDescriptionChange = viewModel::setAudioDescription,
                onVisualAlertsChange = viewModel::setVisualAlerts,
                voiceService = voiceService
            )

            // Interaction Settings
            InteractionSettings(
                settings = accessibilitySettings,
                onScreenReaderChange = viewModel::setScreenReader,
                onButtonSizeChange = viewModel::setButtonSize,
                onTouchDelayChange = viewModel::setTouchDelay,
                onReducedMotionChange = viewModel::setReducedMotion
            )

            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(if (isLoading) "Saving..." else "Save Settings")
                }

                Button(
                    onClick = { viewModel.resetToDefaults() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = !isLoading
                ) {
                    Text("Reset to Defaults")
                }
            }
        }
    }
}

@Composable
fun TextDisplaySettings(
    settings: com.example.icsproject2easenetics.data.models.AccessibilitySettings,
    onTextSizeChange: (com.example.icsproject2easenetics.data.models.TextSize) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onColorBlindModeChange: (com.example.icsproject2easenetics.data.models.ColorBlindMode) -> Unit,
    onSimplifiedLayoutChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TextFields, "Text Display", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Text & Display",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Text Size
            Text("Text Size", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            TextSizeSelector(
                currentSize = settings.textSize,
                onSizeSelected = onTextSizeChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // High Contrast
            SettingToggle(
                icon = Icons.Filled.Visibility,
                title = "High Contrast Mode",
                description = "Increase contrast for better visibility",
                isEnabled = settings.highContrast,
                onToggle = onHighContrastChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Color Blind Mode
            ColorBlindModeSelector(
                currentMode = settings.colorBlindMode,
                onModeSelected = onColorBlindModeChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Simplified Layout
            SettingToggle(
                icon = Icons.Filled.Palette,
                title = "Simplified Layout",
                description = "Cleaner interface with fewer elements",
                isEnabled = settings.simplifiedLayout,
                onToggle = onSimplifiedLayoutChange
            )
        }
    }
}

@Composable
fun AudioVoiceSettings(
    settings: com.example.icsproject2easenetics.data.models.AccessibilitySettings,
    onVoiceNarrationChange: (Boolean) -> Unit,
    onAudioDescriptionChange: (Boolean) -> Unit,
    onVisualAlertsChange: (Boolean) -> Unit,
    voiceService: VoiceService
) {
    var speechRate by remember { mutableStateOf(voiceService.getSpeechRate()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.VolumeUp, "Audio Voice", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Audio & Voice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Narration
            SettingToggle(
                icon = Icons.Filled.VolumeUp,
                title = "Voice Narration",
                description = "Read lesson content aloud",
                isEnabled = settings.voiceNarration,
                onToggle = onVoiceNarrationChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Rate (only show if voice narration is enabled)
            if (settings.voiceNarration) {
                Text(
                    text = "Speech Speed: ${"%.1f".format(speechRate)}x",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = speechRate,
                    onValueChange = { newRate ->
                        speechRate = newRate
                        voiceService.setSpeechRate(newRate)
                    },
                    valueRange = 0.5f..2.0f,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Slower", style = MaterialTheme.typography.bodySmall)
                    Text("Faster", style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Audio Description
            SettingToggle(
                icon = Icons.Filled.Hearing,
                title = "Audio Descriptions",
                description = "Describe visual elements",
                isEnabled = settings.audioDescription,
                onToggle = onAudioDescriptionChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Alerts
            SettingToggle(
                icon = Icons.Filled.Visibility,
                title = "Visual Alerts",
                description = "Show visual cues for sounds",
                isEnabled = settings.visualAlerts,
                onToggle = onVisualAlertsChange
            )
        }
    }
}

@Composable
fun InteractionSettings(
    settings: com.example.icsproject2easenetics.data.models.AccessibilitySettings,
    onScreenReaderChange: (Boolean) -> Unit,
    onButtonSizeChange: (com.example.icsproject2easenetics.data.models.ButtonSize) -> Unit,
    onTouchDelayChange: (com.example.icsproject2easenetics.data.models.TouchDelay) -> Unit,
    onReducedMotionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.TouchApp, "Interaction", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = "Interaction",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Screen Reader
            SettingToggle(
                icon = Icons.Filled.Accessibility,
                title = "Screen Reader Support",
                description = "Optimize for screen readers",
                isEnabled = settings.screenReader,
                onToggle = onScreenReaderChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Button Size
            Text("Button Size", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            ButtonSizeSelector(
                currentSize = settings.buttonSize,
                onSizeSelected = onButtonSizeChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Touch Delay
            Text("Touch Delay", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            TouchDelaySelector(
                currentDelay = settings.touchDelay,
                onDelaySelected = onTouchDelayChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reduced Motion
            SettingToggle(
                icon = Icons.Filled.TouchApp,
                title = "Reduced Motion",
                description = "Minimize animations and transitions",
                isEnabled = settings.reducedMotion,
                onToggle = onReducedMotionChange
            )
        }
    }
}

@Composable
fun TextSizeSelector(
    currentSize: com.example.icsproject2easenetics.data.models.TextSize,
    onSizeSelected: (com.example.icsproject2easenetics.data.models.TextSize) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        com.example.icsproject2easenetics.data.models.TextSize.values().forEach { size ->
            val isSelected = currentSize == size
            Card(
                onClick = { onSizeSelected(size) },
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (size) {
                            com.example.icsproject2easenetics.data.models.TextSize.SMALL -> "A"
                            com.example.icsproject2easenetics.data.models.TextSize.MEDIUM -> "A"
                            com.example.icsproject2easenetics.data.models.TextSize.LARGE -> "A"
                            com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE -> "A"
                        },
                        style = when (size) {
                            com.example.icsproject2easenetics.data.models.TextSize.SMALL -> MaterialTheme.typography.bodyMedium
                            com.example.icsproject2easenetics.data.models.TextSize.MEDIUM -> MaterialTheme.typography.bodyLarge
                            com.example.icsproject2easenetics.data.models.TextSize.LARGE -> MaterialTheme.typography.titleMedium
                            com.example.icsproject2easenetics.data.models.TextSize.EXTRA_LARGE -> MaterialTheme.typography.titleLarge
                        }
                    )
                    Text(
                        text = size.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@Composable
fun ButtonSizeSelector(
    currentSize: com.example.icsproject2easenetics.data.models.ButtonSize,
    onSizeSelected: (com.example.icsproject2easenetics.data.models.ButtonSize) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        com.example.icsproject2easenetics.data.models.ButtonSize.values().forEach { size ->
            val isSelected = currentSize == size
            Card(
                onClick = { onSizeSelected(size) },
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (size) {
                            com.example.icsproject2easenetics.data.models.ButtonSize.SMALL -> "S"
                            com.example.icsproject2easenetics.data.models.ButtonSize.MEDIUM -> "M"
                            com.example.icsproject2easenetics.data.models.ButtonSize.LARGE -> "L"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = size.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorBlindModeSelector(
    currentMode: com.example.icsproject2easenetics.data.models.ColorBlindMode,
    onModeSelected: (com.example.icsproject2easenetics.data.models.ColorBlindMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Color Blind Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = when (currentMode) {
                    com.example.icsproject2easenetics.data.models.ColorBlindMode.NONE -> "None"
                    com.example.icsproject2easenetics.data.models.ColorBlindMode.PROTANOPIA -> "Protanopia (Red-Blind)"
                    com.example.icsproject2easenetics.data.models.ColorBlindMode.DEUTERANOPIA -> "Deuteranopia (Green-Blind)"
                    com.example.icsproject2easenetics.data.models.ColorBlindMode.TRITANOPIA -> "Tritanopia (Blue-Blind)"
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                com.example.icsproject2easenetics.data.models.ColorBlindMode.values().forEach { mode ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (mode) {
                                    com.example.icsproject2easenetics.data.models.ColorBlindMode.NONE -> "None"
                                    com.example.icsproject2easenetics.data.models.ColorBlindMode.PROTANOPIA -> "Protanopia (Red-Blind)"
                                    com.example.icsproject2easenetics.data.models.ColorBlindMode.DEUTERANOPIA -> "Deuteranopia (Green-Blind)"
                                    com.example.icsproject2easenetics.data.models.ColorBlindMode.TRITANOPIA -> "Tritanopia (Blue-Blind)"
                                }
                            )
                        },
                        onClick = {
                            onModeSelected(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TouchDelaySelector(
    currentDelay: com.example.icsproject2easenetics.data.models.TouchDelay,
    onDelaySelected: (com.example.icsproject2easenetics.data.models.TouchDelay) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        com.example.icsproject2easenetics.data.models.TouchDelay.values().forEach { delay ->
            val isSelected = currentDelay == delay
            Card(
                onClick = { onDelaySelected(delay) },
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (delay) {
                            com.example.icsproject2easenetics.data.models.TouchDelay.NONE -> "0s"
                            com.example.icsproject2easenetics.data.models.TouchDelay.SHORT -> "0.5s"
                            com.example.icsproject2easenetics.data.models.TouchDelay.NORMAL -> "1s"
                            com.example.icsproject2easenetics.data.models.TouchDelay.LONG -> "2s"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = delay.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.size(4.dp))
        }
    }
}

@Composable
fun SettingToggle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, title, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall)
            }
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}