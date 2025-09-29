package com.example.icsproject2easenetics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.icsproject2easenetics.data.repositories.FirebaseTestRepository
import com.example.icsproject2easenetics.ui.navigation.AppNavigation
import com.example.icsproject2easenetics.ui.theme.EaseneticsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EaseneticsTheme {
                Surface {
                    FirebaseTestWrapper()
                }
            }
        }
    }
}

@Composable
fun FirebaseTestWrapper() {
    val (isFirebaseConnected, setIsFirebaseConnected) = remember { mutableStateOf<Boolean?>(null) }
    val (testMessage, setTestMessage) = remember { mutableStateOf("Testing Firebase connection...") }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                setTestMessage("Initializing Firebase test...")

                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()
                val testRepository = FirebaseTestRepository(auth, firestore)

                setTestMessage("Testing Firestore write operation...")
                val writeSuccess = testRepository.testFirebaseConnection()

                if (writeSuccess) {
                    setTestMessage("Testing Firestore read operation...")
                    val readSuccess = testRepository.testFirebaseRead()

                    if (readSuccess) {
                        setTestMessage("✅ Firebase connected successfully!")
                        setIsFirebaseConnected(true)
                    } else {
                        setTestMessage("❌ Firebase read test failed")
                        setIsFirebaseConnected(false)
                    }
                } else {
                    setTestMessage("❌ Firebase write test failed")
                    setIsFirebaseConnected(false)
                }
            } catch (e: Exception) {
                setTestMessage("❌ Firebase test error: ${e.message}")
                setIsFirebaseConnected(false)
            }
        }
    }

    when (isFirebaseConnected) {
        true -> {
            // Firebase is connected - show the actual app
            EaseneticsApp()
        }
        false -> {
            // Firebase connection failed - show error
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = testMessage,
                    color = if (isFirebaseConnected == true) Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        null -> {
            // Still testing - show loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = testMessage,
                    style = MaterialTheme.typography.bodyLarge
                )
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