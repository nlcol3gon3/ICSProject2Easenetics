package com.example.icsproject2easenetics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.theme.EaseneticsTheme
import com.example.icsproject2easenetics.ui.navigation.AppNavigation
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel

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
    val userViewModel: UserViewModel = viewModel()
    val currentUser by userViewModel.currentUser.collectAsState()

    AppNavigation(currentUser = currentUser)
}

@Preview(showBackground = true)
@Composable
fun EaseneticsAppPreview() {
    EaseneticsTheme {
        EaseneticsApp()
    }
}