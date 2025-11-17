package com.example.icsproject2easenetics.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.utils.FirebaseDataPopulator
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class AdminUploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoading by remember { mutableStateOf(false) }
            var uploadStatus by remember { mutableStateOf<String?>(null) }
            var debugInfo by remember { mutableStateOf<String?>(null) }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Firebase Data Upload",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "⚠️ Make sure Firestore rules allow writes:\n" +
                                "Go to Firebase Console → Firestore → Rules\n" +
                                "Set rules to allow read/write for development",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            uploadStatus = "Starting upload process..."
                            debugInfo = null

                            lifecycleScope.launch {
                                try {
                                    val populator = FirebaseDataPopulator()

                                    uploadStatus = "Uploading modules..."
                                    val modulesSuccess = populator.populateModules()

                                    if (modulesSuccess) {
                                        uploadStatus = "✅ Modules uploaded!\nUploading lessons..."
                                        val lessonsSuccess = populator.populateLessons()

                                        if (lessonsSuccess) {
                                            uploadStatus = "✅ Lessons uploaded!\nUploading quiz questions..."
                                            val questionsSuccess = populator.populateQuizQuestions()

                                            if (questionsSuccess) {
                                                uploadStatus = "✅ All data uploaded successfully!\n\n• 5 Modules\n• 20 Lessons\n• 40 Quiz Questions"
                                                debugInfo = "Check Firebase Console to verify data"
                                            } else {
                                                uploadStatus = "❌ Failed to upload quiz questions"
                                            }
                                        } else {
                                            uploadStatus = "❌ Failed to upload lessons"
                                        }
                                    } else {
                                        uploadStatus = "❌ Failed to upload modules\n\nCheck Firestore security rules!"
                                        debugInfo = "Make sure rules allow read/write access"
                                    }
                                } catch (e: Exception) {
                                    uploadStatus = "❌ Exception occurred during upload"
                                    debugInfo = "Error: ${e.message}\n\nSolution: Update Firestore security rules to allow writes"
                                    e.printStackTrace()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Upload All Data to Firebase")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    uploadStatus?.let { status ->
                        Text(
                            text = status,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    debugInfo?.let { info ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = info,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}