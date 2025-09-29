package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.data.models.LessonCategory
import com.example.icsproject2easenetics.ui.components.LessonCard
import com.example.icsproject2easenetics.ui.components.ProgressCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLessonClick: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Easenetics",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onChatbotClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Chat, "AI Assistant")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
                            "Welcome to Easenetics!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Learn digital skills at your own pace with our easy-to-follow lessons",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                // Progress Overview
                ProgressCard(
                    completedLessons = 3,
                    totalLessons = 10,
                    averageScore = 85
                )
            }

            item {
                Text(
                    "Featured Lessons",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sample Lessons
            items(5) { index ->
                LessonCard(
                    title = when (index) {
                        0 -> "Getting Started with Your Smartphone"
                        1 -> "Safe Internet Browsing"
                        2 -> "Connecting with Family on Social Media"
                        3 -> "Online Safety Basics"
                        4 -> "Using Video Calls"
                        else -> "Digital Skills Lesson"
                    },
                    description = when (index) {
                        0 -> "Learn the basics of using your smartphone"
                        1 -> "Stay safe while browsing the internet"
                        2 -> "Connect with loved ones on social platforms"
                        3 -> "Protect yourself from online threats"
                        4 -> "Make video calls to family and friends"
                        else -> "Improve your digital skills"
                    },
                    duration = "15 min",
                    progress = (index * 25).coerceAtMost(100),
                    category = when (index) {
                        0 -> LessonCategory.SMARTPHONE_BASICS
                        1 -> LessonCategory.INTERNET_BROWSING
                        2 -> LessonCategory.SOCIAL_MEDIA
                        3 -> LessonCategory.ONLINE_SAFETY
                        else -> LessonCategory.COMMUNICATION
                    },
                    onClick = { onLessonClick("lesson_$index") }
                )
            }
        }
    }
}