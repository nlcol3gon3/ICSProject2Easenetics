package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.ui.components.LessonCard
import com.example.icsproject2easenetics.ui.components.ProgressCard
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLessonClick: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // TEMPORARY FIX: Comment out ViewModel for now to get it running
    // val userViewModel: UserViewModel = viewModel()
    // val userProgress by userViewModel.userProgress.collectAsState()
    // val availableLessons by userViewModel.availableLessons.collectAsState()
    // val isLoading by userViewModel.isLoading.collectAsState()

    // Use sample data instead
    val completedLessons = 3
    val totalLessons = 10
    val averageScore = 85

    // Load user progress and lessons when screen is created
    // LaunchedEffect(Unit) {
    //     userViewModel.loadUserProgress("current_user_id")
    //     userViewModel.loadAvailableLessons()
    // }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Easenetics",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatbotClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Chat, "AI Assistant")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Welcome Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Welcome to Easenetics!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Learn digital skills at your own pace with our easy-to-follow lessons",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                // Progress Overview
                ProgressCard(
                    completedLessons = completedLessons,
                    totalLessons = totalLessons,
                    averageScore = averageScore
                )
            }

            item {
                Text(
                    text = "Featured Lessons",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sample lessons data
            val sampleLessons = listOf(
                SampleLesson("lesson_smartphone_basics", "Getting Started with Your Smartphone", "Learn the basics of using your smartphone", 15, 0),
                SampleLesson("lesson_internet_safety", "Safe Internet Browsing", "Stay safe while browsing the internet", 20, 25),
                SampleLesson("lesson_social_media", "Connecting with Family on Social Media", "Connect with loved ones on social platforms", 25, 50),
                SampleLesson("lesson_online_safety", "Online Safety Basics", "Protect yourself from online threats", 18, 75),
                SampleLesson("lesson_video_calls", "Using Video Calls", "Make video calls to family and friends", 22, 100)
            )

            itemsIndexed(sampleLessons) { index, lesson ->
                LessonCard(
                    title = lesson.title,
                    description = lesson.description,
                    duration = "${lesson.duration} min",
                    progress = lesson.progress,
                    category = index,
                    onClick = { onLessonClick(lesson.id) }
                )
            }
        }
    }
}

// Helper data class for sample lessons
private data class SampleLesson(
    val id: String,
    val title: String,
    val description: String,
    val duration: Int,
    val progress: Int
)