package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import com.example.icsproject2easenetics.ui.components.AccessibleButton
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ProgressViewModel
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val learningStats by progressViewModel.learningStats.collectAsState()
    val achievements by progressViewModel.achievements.collectAsState()
    val progressLoading by progressViewModel.isLoading.collectAsState()
    val userProgress by userViewModel.userProgress.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    // Load progress data when screen opens
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            progressViewModel.loadProgressData(userId)
            userViewModel.loadUserProgress(userId)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "My Progress",
                        style = AccessibilityManager.getScaledTitleLarge(),
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
        if (progressLoading) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading your progress...",
                    style = AccessibilityManager.getScaledBodyMedium()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Tab Row
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        text = { Text("Overview") },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    Tab(
                        text = { Text("Achievements") },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    Tab(
                        text = { Text("Statistics") },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> ProgressOverviewTab(
                        learningStats = learningStats,
                        achievements = achievements,
                        userProgress = userProgress,
                        onRefresh = {
                            currentUser?.uid?.let { userId ->
                                progressViewModel.loadProgressData(userId)
                                userViewModel.loadUserProgress(userId)
                            }
                        }
                    )
                    1 -> AchievementsTab(
                        achievements = achievements,
                        learningStats = learningStats
                    )
                    2 -> StatisticsTab(
                        learningStats = learningStats,
                        userProgress = userProgress
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressOverviewTab(
    learningStats: com.example.icsproject2easenetics.data.models.LearningStats?,
    achievements: List<com.example.icsproject2easenetics.data.models.Achievement>,
    userProgress: List<com.example.icsproject2easenetics.data.models.UserProgress>,
    onRefresh: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Motivational Message
            learningStats?.let { stats ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Celebration,
                                contentDescription = "Celebration",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Keep Going!",
                                style = AccessibilityManager.getScaledTitleMedium(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = getMotivationalMessage(stats),
                            style = AccessibilityManager.getScaledBodyMedium(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        item {
            // Quick Stats
            Text(
                text = "Quick Stats",
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Lessons Completed
                StatCard(
                    title = "Lessons",
                    value = learningStats?.totalLessonsCompleted?.toString() ?: "0",
                    icon = Icons.Filled.School,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Quizzes Taken
                StatCard(
                    title = "Quizzes",
                    value = learningStats?.totalQuizzesTaken?.toString() ?: "0",
                    icon = Icons.Filled.Quiz,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Average Score
                StatCard(
                    title = "Avg Score",
                    value = "${learningStats?.averageQuizScore?.toInt() ?: 0}%",
                    icon = Icons.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )

                // Learning Time
                StatCard(
                    title = "Time",
                    value = formatLearningTime(learningStats?.totalLearningTime ?: 0),
                    icon = Icons.Filled.Timer,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // Streak Information
            learningStats?.let { stats ->
                if (stats.currentStreak > 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🔥 Learning Streak",
                                    style = AccessibilityManager.getScaledTitleMedium(),
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "${stats.currentStreak} days in a row!",
                                    style = AccessibilityManager.getScaledBodyMedium(),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                if (stats.longestStreak > stats.currentStreak) {
                                    Text(
                                        text = "Longest streak: ${stats.longestStreak} days",
                                        style = AccessibilityManager.getScaledBodySmall(),
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(60.dp)
                            ) {
                                Text(
                                    text = stats.currentStreak.toString(),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // Recent Achievements Preview
            val unlockedAchievements = achievements.filter { it.unlocked }
            if (unlockedAchievements.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Achievements",
                                style = AccessibilityManager.getScaledTitleLarge(),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${unlockedAchievements.size}/${achievements.size}",
                                style = AccessibilityManager.getScaledBodyMedium(),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show 3 most recent achievements
                        unlockedAchievements.take(3).forEach { achievement ->
                            AchievementProgressRow(achievement = achievement)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (unlockedAchievements.size > 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "And ${unlockedAchievements.size - 3} more achievements unlocked!",
                                style = AccessibilityManager.getScaledBodySmall(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        item {
            // Skills Mastered
            learningStats?.let { stats ->
                if (stats.skillsMastered.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Skills Mastered",
                                style = AccessibilityManager.getScaledTitleLarge(),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                stats.skillsMastered.forEach { skill ->
                                    SkillRow(skillName = skill)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            // Refresh Button
            AccessibleButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Refresh Progress Data")
            }
        }
    }
}

@Composable
fun AchievementsTab(
    achievements: List<com.example.icsproject2easenetics.data.models.Achievement>,
    learningStats: com.example.icsproject2easenetics.data.models.LearningStats?
) {
    val unlockedAchievements = achievements.filter { it.unlocked }
    val lockedAchievements = achievements.filter { !it.unlocked }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Achievement Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AchievementSummaryStat(
                    value = unlockedAchievements.size.toString(),
                    label = "Unlocked",
                    color = MaterialTheme.colorScheme.primary
                )
                AchievementSummaryStat(
                    value = "${((unlockedAchievements.size.toDouble() / achievements.size) * 100).toInt()}%",
                    label = "Completed",
                    color = MaterialTheme.colorScheme.secondary
                )
                AchievementSummaryStat(
                    value = lockedAchievements.size.toString(),
                    label = "Remaining",
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        // Unlocked Achievements
        if (unlockedAchievements.isNotEmpty()) {
            Text(
                text = "Unlocked Achievements (${unlockedAchievements.size})",
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )

            unlockedAchievements.forEach { achievement ->
                AchievementCard(achievement = achievement, isUnlocked = true)
            }
        }

        // Locked Achievements
        if (lockedAchievements.isNotEmpty()) {
            Text(
                text = "Next Achievements (${lockedAchievements.size})",
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )

            lockedAchievements.forEach { achievement ->
                AchievementCard(achievement = achievement, isUnlocked = false)
            }
        }

        if (achievements.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Achievements Yet",
                        style = AccessibilityManager.getScaledTitleMedium(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete lessons and quizzes to unlock achievements!",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatisticsTab(
    learningStats: com.example.icsproject2easenetics.data.models.LearningStats?,
    userProgress: List<com.example.icsproject2easenetics.data.models.UserProgress>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        learningStats?.let { stats ->
            // Learning Statistics
            Text(
                text = "Learning Statistics",
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )

            // Completion Rate
            StatisticCard(
                title = "Completion Rate",
                value = if (stats.totalLessonsCompleted > 0) {
                    "${((stats.totalLessonsCompleted.toDouble() / 20) * 100).toInt()}%"
                } else "0%",
                description = "${stats.totalLessonsCompleted} out of 20 lessons completed",
                progress = stats.totalLessonsCompleted.toFloat() / 20f
            )

            // Quiz Performance
            StatisticCard(
                title = "Quiz Performance",
                value = "${stats.averageQuizScore.toInt()}%",
                description = "Average score across ${stats.totalQuizzesTaken} quizzes",
                progress = stats.averageQuizScore.toFloat() / 100f
            )

            // Learning Time
            StatisticCard(
                title = "Total Learning Time",
                value = formatLearningTime(stats.totalLearningTime),
                description = "Time spent on lessons and quizzes",
                progress = minOf(stats.totalLearningTime.toFloat() / 600f, 1f)
            )

            // Streak Statistics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Current Streak",
                    value = "${stats.currentStreak} days",
                    icon = Icons.Filled.TrendingUp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Longest Streak",
                    value = "${stats.longestStreak} days",
                    icon = Icons.Filled.Celebration,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Recent Activity
            Text(
                text = "Recent Activity",
                style = AccessibilityManager.getScaledTitleLarge(),
                fontWeight = FontWeight.Bold
            )

            val recentProgress = userProgress.sortedByDescending { it.lastAccessed }.take(5)
            if (recentProgress.isNotEmpty()) {
                recentProgress.forEach { progress ->
                    RecentActivityRow(progress = progress)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Recent Activity",
                            style = AccessibilityManager.getScaledBodyMedium()
                        )
                        Text(
                            text = "Start learning to see your activity here!",
                            style = AccessibilityManager.getScaledBodySmall(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } ?: run {
            // No stats available
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Statistics Available",
                        style = AccessibilityManager.getScaledTitleMedium(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete some lessons and quizzes to see your statistics",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = AccessibilityManager.getScaledTitleMedium(),
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = AccessibilityManager.getScaledBodySmall(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AchievementSummaryStat(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = AccessibilityManager.getScaledTitleLarge(),
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = AccessibilityManager.getScaledBodySmall(),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AchievementCard(
    achievement: com.example.icsproject2easenetics.data.models.Achievement,
    isUnlocked: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement Icon
            Text(
                text = achievement.iconRes,
                style = AccessibilityManager.getScaledTitleLarge(),
                modifier = Modifier.padding(end = 16.dp)
            )

            // Achievement Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = achievement.title,
                    style = AccessibilityManager.getScaledTitleMedium(),
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = achievement.description,
                    style = AccessibilityManager.getScaledBodyMedium(),
                    color = if (isUnlocked) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Progress for locked achievements
                if (!isUnlocked && achievement.target > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text(
                            text = "Progress: ${achievement.currentProgress}/${achievement.target}",
                            style = AccessibilityManager.getScaledBodySmall(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = achievement.currentProgress.toFloat() / achievement.target.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Status Indicator
            if (isUnlocked) {
                Text(
                    text = "✓",
                    style = AccessibilityManager.getScaledTitleMedium(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AchievementProgressRow(achievement: com.example.icsproject2easenetics.data.models.Achievement) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = achievement.iconRes,
            style = AccessibilityManager.getScaledBodyLarge(),
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = achievement.title,
                style = AccessibilityManager.getScaledBodyMedium(),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = achievement.description,
                style = AccessibilityManager.getScaledBodySmall(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SkillRow(skillName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✓",
                style = AccessibilityManager.getScaledBodyMedium(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = skillName,
                style = AccessibilityManager.getScaledBodyMedium(),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    description: String,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AccessibilityManager.getScaledTitleMedium(),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    style = AccessibilityManager.getScaledTitleMedium(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = AccessibilityManager.getScaledBodyMedium(),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun RecentActivityRow(progress: com.example.icsproject2easenetics.data.models.UserProgress) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (progress.completed) Icons.Filled.School else Icons.Filled.Quiz,
                contentDescription = if (progress.completed) "Lesson" else "Quiz",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (progress.completed) "Lesson Completed" else "Quiz Attempted",
                    style = AccessibilityManager.getScaledBodyMedium(),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(progress.lastAccessed),
                    style = AccessibilityManager.getScaledBodySmall(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (progress.score > 0) {
                Text(
                    text = "${progress.score}%",
                    style = AccessibilityManager.getScaledBodyMedium(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Helper functions
private fun getMotivationalMessage(stats: com.example.icsproject2easenetics.data.models.LearningStats): String {
    return when {
        stats.currentStreak >= 7 -> "🔥 Amazing streak! You're on fire!"
        stats.averageQuizScore >= 90 -> "💯 Excellent scores! You're mastering digital skills!"
        stats.totalLessonsCompleted >= 10 -> "🚀 Great progress! You've completed ${stats.totalLessonsCompleted} lessons!"
        stats.totalLearningTime >= 60 -> "⏰ You've invested ${stats.totalLearningTime} minutes in learning - great commitment!"
        stats.totalLessonsCompleted >= 5 -> "🌟 You're making great progress! Keep learning!"
        else -> "🌟 Welcome! Start your digital learning journey today!"
    }
}

private fun formatLearningTime(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
    return format.format(date)
}