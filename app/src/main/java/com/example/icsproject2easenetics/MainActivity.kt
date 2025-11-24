package com.example.icsproject2easenetics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.Context
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
                Surface(color = MaterialTheme.colorScheme.background) {
                    EaseneticsApp()
                }
            }
        }
    }
}

@Composable
fun EaseneticsApp() {
    val context = LocalContext.current // Add this
    val accessibilityViewModel: AccessibilityViewModel = viewModel()
    val accessibilitySettings by accessibilityViewModel.accessibilitySettings.collectAsState()

    LaunchedEffect(accessibilitySettings) {
        AccessibilityManager.updateSettings(accessibilitySettings)
    }

    CompositionLocalProvider(
        LocalAccessibilitySettings provides accessibilitySettings
    ) {
        AppNavigation(
            context = context // Use the actual context
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EaseneticsAppPreview() {
    EaseneticsTheme {
        EaseneticsApp()
    }
}
