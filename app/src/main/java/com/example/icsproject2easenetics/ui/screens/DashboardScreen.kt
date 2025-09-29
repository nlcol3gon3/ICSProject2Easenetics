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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person // ADD THIS IMPORT
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // ADD THIS IMPORT
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
                actions = {
                    // ADD PROFILE BUTTON - FIXED IMPORTS
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            Icons.Filled.Person, // Using Person icon instead of Settings
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
                    category = index,
                    onClick = { onLessonClick("lesson_$index") }
                )
            }
        }
    }
}