// DashboardScreen.kt (Updated)
package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
    onProfileClick: () -> Unit,
    onProgressClick: () -> Unit,
    onModulesClick: () -> Unit // NEW: Added modules navigation
) {
    // TEMPORARY FIX: Use sample data
    val completedLessons = 3
    val totalLessons = 10
    val averageScore = 85

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
                    // Progress Button
                    IconButton(onClick = onProgressClick) {
                        Icon(
                            Icons.Filled.TrendingUp,
                            contentDescription = "Progress"
                        )
                    }
                    // Profile Button
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
                            text = "Karibu Easenetics!",
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

            // NEW: Browse Modules Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Structured Learning",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Follow our step-by-step modules designed specifically for Kenyan seniors",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onModulesClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.CollectionsBookmark,
                                    contentDescription = "Modules",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text("Browse All Learning Modules")
                            }
                        }
                    }
                }
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
                SampleLesson("lesson_1_1", "Meet Your Smartphone", "Learn the physical parts and basic navigation", 15, 0),
                SampleLesson("lesson_3_1", "M-Pesa Basics", "Understand and access your digital wallet", 20, 25),
                SampleLesson("lesson_4_1", "Spotting M-Pesa Scams", "Identify and avoid common fraud attempts", 25, 50),
                SampleLesson("lesson_5_1", "eCitizen Registration", "Create your government services account", 18, 75)
            )

            itemsIndexed(sampleLessons) { index, lesson ->
                LessonCard(
                    title = lesson.title,
                    description = lesson.description,
                    duration = "${lesson.duration} min",
                    progress = lesson.progress,
                    category = getCategoryForLesson(lesson.id),
                    onClick = { onLessonClick(lesson.id) }
                )
            }
        }
    }
}

// Helper function to determine category for lesson
private fun getCategoryForLesson(lessonId: String): Int {
    return when {
        lessonId.startsWith("lesson_1") -> 0 // Smartphone
        lessonId.startsWith("lesson_2") -> 1 // Communication
        lessonId.startsWith("lesson_3") -> 2 // M-Pesa
        lessonId.startsWith("lesson_4") -> 3 // Safety
        lessonId.startsWith("lesson_5") -> 4 // Government
        else -> 0
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