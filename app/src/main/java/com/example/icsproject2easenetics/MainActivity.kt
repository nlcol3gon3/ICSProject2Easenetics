package com.example.icsproject2easenetics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.icsproject2easenetics.ui.navigation.AppNavigation
import com.example.icsproject2easenetics.ui.theme.EaseneticsTheme

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
    AppNavigation()
}

@Preview(showBackground = true)
@Composable
fun EaseneticsAppPreview() {
    EaseneticsTheme {
        EaseneticsApp()
    }
}