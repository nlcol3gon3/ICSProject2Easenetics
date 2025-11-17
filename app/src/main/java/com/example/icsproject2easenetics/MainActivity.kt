package com.example.icsproject2easenetics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.navigation.AppNavigation
import com.example.icsproject2easenetics.ui.theme.EaseneticsTheme
import com.example.icsproject2easenetics.ui.viewmodels.AccessibilityViewModel
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.example.icsproject2easenetics.utils.LocalAccessibilitySettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EaseneticsTheme {
                Surface {
                    EaseneticsApp()
                }
            }
        }
    }
}

@Composable
fun EaseneticsApp() {
    val accessibilityViewModel: AccessibilityViewModel = viewModel()
    val accessibilitySettings by accessibilityViewModel.accessibilitySettings.collectAsState()

    // Update global accessibility manager when settings change
    LaunchedEffect(accessibilitySettings) {
        AccessibilityManager.updateSettings(accessibilitySettings)
    }

    // Provide accessibility settings to the entire app
    androidx.compose.runtime.CompositionLocalProvider(
        LocalAccessibilitySettings provides accessibilitySettings
    ) {
        AppNavigation()
    }
}

@Preview(showBackground = true)
@Composable
fun EaseneticsAppPreview() {
    EaseneticsTheme {
        EaseneticsApp()
    }
}