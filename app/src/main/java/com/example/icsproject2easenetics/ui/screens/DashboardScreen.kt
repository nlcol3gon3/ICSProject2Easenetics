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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ModuleViewModel
import com.example.icsproject2easenetics.ui.viewmodels.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLessonClick: (String) -> Unit,
    onChatbotClick: () -> Unit,
    onProfileClick: () -> Unit,
    onProgressClick: () -> Unit,
    onModulesClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    moduleViewModel: ModuleViewModel = viewModel(),
    lessonViewModel: LessonViewModel = viewModel()
) {
    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()

    // Get real data from ViewModels
    val availableLessons by userViewModel.availableLessons.collectAsState()
    val userProgress by userViewModel.userProgress.collectAsState()
    val userLoading by userViewModel.isLoading.collectAsState()
    val userError by userViewModel.errorMessage.collectAsState()

    // Get modules data for real lesson counts
    val modules by moduleViewModel.modules.collectAsState()
    val moduleLessons by moduleViewModel.moduleLessons.collectAsState()
    val modulesLoading by moduleViewModel.isLoading.collectAsState()
    val modulesError by moduleViewModel.errorMessage.collectAsState()

    // Extract first name from user's email or display name
    val firstName = extractUserName(currentUser)

    // Load real data when screen opens
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            println("🔄 DashboardScreen: Loading data for user: $userId")

            // Set current user in LessonViewModel for progress tracking
            lessonViewModel.setCurrentUser(userId)

            // Load user progress and available lessons
            userViewModel.loadUserProgress(userId)
            userViewModel.loadAvailableLessons()

            // Load modules and lessons from Firebase
            moduleViewModel.loadAllModules()
        }
    }

    // Calculate REAL progress from actual user data
    val completedLessons = userViewModel.getCompletedLessonsCount()

    // Calculate total lessons from actual Firebase data
    val totalLessons = modules.flatMap { module ->
        moduleLessons[module.moduleId] ?: emptyList()
    }.size

    val averageScore = userViewModel.getAverageScore()
    val totalLearningTime = userViewModel.getTotalLearningTime()

    // Get featured lessons from actual data (mix of completed and new lessons)
    val featuredLessons = getFeaturedLessons(
        modules = modules,
        moduleLessons = moduleLessons,
        userProgress = userProgress,
        availableLessons = availableLessons
    )

    val isLoading = userLoading || modulesLoading
    val hasError = userError != null || modulesError != null

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
                Icon(Icons.Filled.Chat, "Mshauri, Your AI Assistant")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            // Loading state
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
                    text = "Loading your learning dashboard...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "📊 Loading modules: ${modules.size} • Lessons: ${availableLessons.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else if (hasError) {
            // Error state
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Connection Issue",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = userError ?: modulesError ?: "Unable to load data",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                currentUser?.uid?.let { userId ->
                                    userViewModel.loadUserProgress(userId)
                                    userViewModel.loadAvailableLessons()
                                    moduleViewModel.loadAllModules()
                                }
                            }
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        } else {
            // Success state - Show real data
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    // Welcome Card with real user data
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
                                text = if (firstName.isNotEmpty()) "Karibu $firstName!" else "Karibu Easenetics!",
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

                            // Show real data summary
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                DashboardStat("Modules", "${modules.size}")
                                DashboardStat("Lessons", "$totalLessons")
                                DashboardStat("Completed", "$completedLessons")
                            }
                        }
                    }
                }

                item {
                    // Progress Overview with REAL data
                    ProgressCard(
                        completedLessons = completedLessons,
                        totalLessons = if (totalLessons > 0) totalLessons else 1, // Avoid division by zero
                        averageScore = averageScore
                    )

                    // Additional progress info
                    if (totalLearningTime > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Learning Time",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = formatLearningTime(totalLearningTime),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Browse Modules Section with real module count
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

                            Spacer(modifier = Modifier.height(8.dp))

                            // Show real module statistics
                            if (modules.isNotEmpty()) {
                                Column {
                                    modules.take(3).forEach { module ->
                                        val moduleLessonCount = moduleLessons[module.moduleId]?.size ?: 0
                                        val completedInModule = userProgress.count { progress ->
                                            moduleLessons[module.moduleId]?.any { it.lessonId == progress.lessonId } == true && progress.completed
                                        }

                                        ModuleProgressRow(
                                            moduleName = module.title,
                                            completed = completedInModule,
                                            total = moduleLessonCount
                                        )
                                    }

                                    if (modules.size > 3) {
                                        Text(
                                            text = "... and ${modules.size - 3} more modules",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                }
                            }

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
                                    Text("Browse All ${modules.size} Modules")
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

                if (featuredLessons.isNotEmpty()) {
                    items(featuredLessons) { lesson ->
                        // Get progress for this specific lesson
                        val lessonProgress = userProgress.find { it.lessonId == lesson.lessonId }
                        val progressPercentage = if (lessonProgress?.completed == true) 100 else 0
                        val score = lessonProgress?.score ?: 0

                        LessonCard(
                            title = lesson.title,
                            description = lesson.objective.ifEmpty { lesson.description },
                            duration = "${lesson.duration} min",
                            progress = progressPercentage,
                            category = getCategoryForLesson(lesson.lessonId),
                            onClick = { onLessonClick(lesson.lessonId) }
                        )
                    }
                } else {
                    item {
                        // No lessons available
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Lessons Available",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Lessons will appear here once they are loaded from Firebase",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper function to extract user name
private fun extractUserName(currentUser: com.google.firebase.auth.FirebaseUser?): String {
    return when {
        currentUser?.displayName?.isNotEmpty() == true -> currentUser.displayName!!
        currentUser?.email?.isNotEmpty() == true -> {
            currentUser.email!!.split("@").first().split(".").first()
                .replaceFirstChar { it.uppercase() }
        }
        else -> ""
    }
}

// Helper function to determine category for lesson
private fun getCategoryForLesson(lessonId: String): Int {
    return when {
        lessonId.contains("module_1") || lessonId.contains("smartphone") -> 0 // Smartphone
        lessonId.contains("module_2") || lessonId.contains("communication") -> 1 // Communication
        lessonId.contains("module_3") || lessonId.contains("mpesa") -> 2 // M-Pesa
        lessonId.contains("module_4") || lessonId.contains("safety") -> 3 // Safety
        lessonId.contains("module_5") || lessonId.contains("government") -> 4 // Government
        else -> 0
    }
}

// Helper function to format learning time
private fun formatLearningTime(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}

// Helper function to get featured lessons (mix of completed, in-progress, and new)
private fun getFeaturedLessons(
    modules: List<com.example.icsproject2easenetics.data.models.Module>,
    moduleLessons: Map<String, List<com.example.icsproject2easenetics.data.models.Lesson>>,
    userProgress: List<com.example.icsproject2easenetics.data.models.UserProgress>,
    availableLessons: List<com.example.icsproject2easenetics.data.models.Lesson>
): List<com.example.icsproject2easenetics.data.models.Lesson> {
    val allLessons = modules.flatMap { module -> moduleLessons[module.moduleId] ?: emptyList() }

    if (allLessons.isEmpty()) return availableLessons.take(4)

    val completedLessons = allLessons.filter { lesson ->
        userProgress.any { it.lessonId == lesson.lessonId && it.completed }
    }

    val inProgressLessons = allLessons.filter { lesson ->
        userProgress.any { it.lessonId == lesson.lessonId && !it.completed }
    }

    val newLessons = allLessons.filter { lesson ->
        userProgress.none { it.lessonId == lesson.lessonId }
    }

    // Return mix: 1 completed, 1 in-progress, 2 new (or whatever available)
    return (completedLessons.take(1) + inProgressLessons.take(1) + newLessons.take(2))
        .take(4)
        .distinctBy { it.lessonId }
}

// Composable for dashboard statistics
@Composable
private fun DashboardStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

// Composable for module progress row
@Composable
private fun ModuleProgressRow(moduleName: String, completed: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = moduleName,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$completed/$total",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}