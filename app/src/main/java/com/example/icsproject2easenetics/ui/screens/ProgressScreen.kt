package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.example.icsproject2easenetics.ui.viewmodels.ProgressViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = viewModel()
) {
    val learningStats by viewModel.learningStats.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val weeklyProgress by viewModel.weeklyProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Get current user directly from Firebase Auth
    val currentUser = Firebase.auth.currentUser

    // Load REAL progress data when screen opens
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            viewModel.loadProgressData(userId) // Use actual user ID
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Progress",
                        style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Motivational Message
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "🌟 Your Learning Journey",
                                style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = viewModel.getMotivationalMessage(),
                                style = AccessibilityManager.getScaledBodyLarge() // CHANGED
                            )
                        }
                    }
                }

                item {
                    // Learning Stats
                    Text(
                        text = "📊 Learning Statistics",
                        style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    LearningStatsCard(stats = learningStats)
                }

                item {
                    // Achievements
                    Text(
                        text = "🏆 Achievements",
                        style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                        fontWeight = FontWeight.Bold
                    )
                }

                items(achievements) { achievement ->
                    AchievementItem(achievement = achievement)
                }

                item {
                    // Weekly Progress
                    Text(
                        text = "📈 Weekly Progress",
                        style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                        fontWeight = FontWeight.Bold
                    )
                }

                items(weeklyProgress) { weekProgress ->
                    WeeklyProgressItem(progress = weekProgress)
                }
            }
        }
    }
}

@Composable
fun LearningStatsCard(stats: com.example.icsproject2easenetics.data.models.LearningStats?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = stats?.totalLessonsCompleted?.toString() ?: "0",
                    label = "Lessons Completed",
                    icon = "📚"
                )
                StatItem(
                    value = stats?.totalQuizzesTaken?.toString() ?: "0",
                    label = "Quizzes Taken",
                    icon = "🧠"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = String.format("%.1f%%", stats?.averageQuizScore ?: 0.0),
                    label = "Average Score",
                    icon = "💯"
                )
                StatItem(
                    value = "${stats?.totalLearningTime ?: 0}m",
                    label = "Learning Time",
                    icon = "⏰"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    value = stats?.currentStreak?.toString() ?: "0",
                    label = "Current Streak",
                    icon = "🔥"
                )
                StatItem(
                    value = stats?.longestStreak?.toString() ?: "0",
                    label = "Longest Streak",
                    icon = "⭐"
                )
            }

            // Skills Mastered
            if (!stats?.skillsMastered.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "✅ Skills Mastered:",
                    style = AccessibilityManager.getScaledTitleSmall(), // CHANGED
                    fontWeight = FontWeight.Bold
                )
                stats?.skillsMastered?.forEach { skill ->
                    Text(
                        text = "• $skill",
                        style = AccessibilityManager.getScaledBodyMedium(), // CHANGED
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = AccessibilityManager.getScaledTitleLarge() // CHANGED
        )
        Text(
            text = value,
            style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = AccessibilityManager.getScaledBodySmall(), // CHANGED
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementItem(achievement: com.example.icsproject2easenetics.data.models.Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = if (achievement.unlocked) {
            androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            androidx.compose.material3.CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = achievement.iconRes,
                style = AccessibilityManager.getScaledTitleLarge(), // CHANGED
                modifier = Modifier.padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = AccessibilityManager.getScaledTitleMedium(), // CHANGED
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = achievement.description,
                    style = AccessibilityManager.getScaledBodyMedium() // CHANGED
                )

                if (!achievement.unlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = achievement.currentProgress.toFloat() / achievement.target.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${achievement.currentProgress}/${achievement.target}",
                        style = AccessibilityManager.getScaledBodySmall() // CHANGED
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✅ Unlocked!",
                        style = AccessibilityManager.getScaledBodySmall(), // CHANGED
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyProgressItem(progress: com.example.icsproject2easenetics.data.models.WeeklyProgress) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Week Progress",
                    style = AccessibilityManager.getScaledBodyMedium(), // CHANGED
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Score: ${String.format("%.0f%%", progress.averageScore)}",
                    style = AccessibilityManager.getScaledBodyMedium() // CHANGED
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressMetric("📚", "${progress.lessonsCompleted} Lessons")
                ProgressMetric("🧠", "${progress.quizzesTaken} Quizzes")
                ProgressMetric("⏰", "${progress.learningTime}m")
            }
        }
    }
}

@Composable
fun ProgressMetric(icon: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = icon,
            style = AccessibilityManager.getScaledBodyLarge() // CHANGED
        )
        Text(
            text = value,
            style = AccessibilityManager.getScaledBodySmall() // CHANGED
        )
    }
}