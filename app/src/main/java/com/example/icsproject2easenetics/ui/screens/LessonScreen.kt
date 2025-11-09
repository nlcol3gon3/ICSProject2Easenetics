package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.data.local.LocalDataSource
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.models.DifficultyLevel
import com.example.icsproject2easenetics.data.models.LessonCategory
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.service.VoiceService
import com.example.icsproject2easenetics.ui.viewmodels.LessonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lessonId: String,
    onBack: () -> Unit,
    onStartQuiz: (String, List<QuizQuestion>) -> Unit,
    onMarkComplete: () -> Unit
) {
    val context = LocalContext.current
    val voiceService = remember { VoiceService(context) }
    val lessonViewModel: LessonViewModel = viewModel()

    var isNarrating by remember { mutableStateOf(false) }
    val isSpeaking by voiceService.isSpeaking.collectAsState()
    val speakingError by voiceService.speakingError.collectAsState()

    // Load lesson data
    val currentLesson by lessonViewModel.currentLesson.collectAsState()
    val userProgress by lessonViewModel.userProgress.collectAsState()

    // Load lesson when screen opens or lessonId changes
    LaunchedEffect(lessonId) {
        lessonViewModel.loadLesson(lessonId)
    }

    // Stop narration when leaving screen
    LaunchedEffect(Unit) {
        voiceService.stopSpeaking()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = currentLesson?.title ?: "Lesson",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        voiceService.stopSpeaking()
                        onBack()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    // Voice Narration Toggle
                    if (currentLesson != null) {
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    voiceService.stopSpeaking()
                                    isNarrating = false
                                } else {
                                    val narrationText = """
                                        ${currentLesson!!.title}
                                        
                                        ${currentLesson!!.description}
                                        
                                        ${currentLesson!!.content}
                                    """.trimIndent()
                                    voiceService.speak(narrationText)
                                    isNarrating = true
                                }
                            }
                        ) {
                            Icon(
                                if (isSpeaking) Icons.Filled.VolumeOff else Icons.Filled.VolumeUp,
                                contentDescription = if (isSpeaking) "Stop Narration" else "Start Narration",
                                tint = if (isSpeaking) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            if (currentLesson != null) {
                LessonBottomBar(
                    isCompleted = userProgress?.completed ?: false,
                    hasQuiz = currentLesson!!.hasQuiz,
                    onMarkComplete = {
                        voiceService.stopSpeaking()
                        lessonViewModel.markLessonComplete()
                        onMarkComplete()
                    },
                    onStartQuiz = {
                        voiceService.stopSpeaking()
                        if (currentLesson!!.quizQuestions.isNotEmpty()) {
                            onStartQuiz(currentLesson!!.lessonId, currentLesson!!.quizQuestions)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Show speaking error if any
            if (!speakingError.isNullOrEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = speakingError!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (currentLesson == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading lesson...",
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LessonContent(
                    lesson = currentLesson!!,
                    progress = userProgress,
                    voiceService = voiceService,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
fun LessonContent(
    lesson: Lesson,
    progress: UserProgress?,
    voiceService: VoiceService,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        if (progress != null) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (progress.completed) "Completed" else "In Progress",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (progress.completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (progress.completed) 1.0f else 0.3f,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Lesson info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About This Lesson",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                LessonMetadata(lesson = lesson)
            }
        }

        // Lesson content
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
                        text = "Lesson Content",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Read content aloud button
                    IconButton(
                        onClick = {
                            voiceService.speak(lesson.content, "lesson_content")
                        }
                    ) {
                        Icon(
                            Icons.Filled.VolumeUp,
                            contentDescription = "Read content aloud",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = lesson.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }
        }

        // Video section (if available)
        lesson.videoUrl?.let { videoUrl ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Video Tutorial",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Watch a step-by-step video demonstration of this lesson",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Video placeholder with play button
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "Play Video",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Video Tutorial",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to play",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { /* Play video */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PlayArrow, "Play Video", modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Watch Video Tutorial")
                    }
                }
            }
        }

        // Quiz reminder (if lesson has quiz)
        if (lesson.hasQuiz && lesson.quizQuestions.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📝 Ready to Test Your Knowledge?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This lesson includes a ${lesson.quizQuestions.size}-question quiz to reinforce what you've learned.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "💡 Tip: Complete the quiz to earn achievements and track your progress!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LessonMetadata(lesson: Lesson) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MetadataRow("Duration", "${lesson.duration} minutes")
        MetadataRow("Difficulty", formatDifficulty(lesson.difficulty))
        MetadataRow("Category", formatCategory(lesson.category))
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LessonBottomBar(
    isCompleted: Boolean,
    hasQuiz: Boolean,
    onMarkComplete: () -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCompleted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, "Completed", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text("Lesson Completed!", fontWeight = FontWeight.Bold)
                        Text(
                            "Great job! You've mastered this topic.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Only show quiz button if lesson has quiz
                    if (hasQuiz) {
                        Button(
                            onClick = onStartQuiz,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Take Quiz", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Button(
                        onClick = onMarkComplete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasQuiz) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (hasQuiz) "Skip & Mark Complete" else "Mark Complete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

// Helper functions for formatting
private fun formatDifficulty(difficulty: DifficultyLevel): String {
    return when (difficulty) {
        DifficultyLevel.BEGINNER -> "🟢 Beginner"
        DifficultyLevel.INTERMEDIATE -> "🟡 Intermediate"
        DifficultyLevel.ADVANCED -> "🔴 Advanced"
    }
}

private fun formatCategory(category: LessonCategory): String {
    return when (category) {
        LessonCategory.SMARTPHONE_BASICS -> "📱 Smartphone Basics"
        LessonCategory.INTERNET_BROWSING -> "🌐 Internet Browsing"
        LessonCategory.SOCIAL_MEDIA -> "👥 Social Media"
        LessonCategory.ONLINE_SAFETY -> "🔒 Online Safety"
        LessonCategory.COMMUNICATION -> "💬 Communication"
    }
}

// Sample data for testing (fallback)
private val sampleLessons = listOf(
    Lesson(
        lessonId = "lesson_smartphone_basics",
        title = "Getting Started with Your Smartphone",
        description = "Learn the basics of using your smartphone",
        content = """
            Welcome to Your Smartphone!
            
            Your smartphone is like a small computer that fits in your pocket. Let's learn the basics:
            
            📱 Basic Functions:
            • **Making Calls**: Tap the phone icon, enter number, tap green call button
            • **Sending Messages**: Tap messages icon, select contact, type your message  
            • **Taking Photos**: Open camera app, point at subject, tap shutter button
            
            🔋 Battery Tips:
            • Charge your phone overnight
            • Dim screen brightness to save battery
            • Close apps you're not using
            
            ⚙️ Basic Settings:
            • **Volume**: Use side buttons to adjust
            • **Wi-Fi**: Swipe down from top, tap Wi-Fi icon
            • **Brightness**: Swipe down from top, adjust slider
            
            Practice these steps with your phone in hand!
        """.trimIndent(),
        duration = 15,
        difficulty = DifficultyLevel.BEGINNER,
        category = LessonCategory.SMARTPHONE_BASICS,
        order = 1,
        hasQuiz = true,
        quizQuestions = listOf(
            QuizQuestion(
                questionId = "q1",
                question = "What is the main purpose of a smartphone?",
                options = listOf(
                    "Communication and internet access",
                    "Only for emergency calls",
                    "Just for taking photos",
                    "Only for games"
                ),
                correctAnswer = 0,
                explanation = "Smartphones are versatile devices for communication, internet, photos, and many useful functions."
            ),
            QuizQuestion(
                questionId = "q2",
                question = "How do you make a phone call?",
                options = listOf(
                    "Tap phone icon, enter number, tap call button",
                    "Shake the phone and say 'call'",
                    "Press all buttons at once",
                    "Put phone to ear and wait"
                ),
                correctAnswer = 0,
                explanation = "Use the phone app to dial numbers and make calls safely."
            )
        )
    ),
    Lesson(
        lessonId = "lesson_internet_safety",
        title = "Safe Internet Browsing",
        description = "Stay safe while browsing the internet",
        content = """
            Staying Safe Online
            
            The internet is wonderful but requires caution. Here's how to stay safe:
            
            🔒 Password Security:
            • Use strong, unique passwords
            • Combine letters, numbers, and symbols
            • Never share passwords with anyone
            
            📧 Email Safety:
            • Don't open emails from strangers
            • Never click suspicious links  
            • Look for spelling mistakes in emails
            
            🌐 Website Safety:
            • Look for 🔒 or 'https://' in address bar
            • Avoid entering personal info on unfamiliar sites
            • Use reputable websites for shopping
            
            Remember: If something seems too good to be true, it probably is!
        """.trimIndent(),
        duration = 20,
        difficulty = DifficultyLevel.BEGINNER,
        category = LessonCategory.ONLINE_SAFETY,
        order = 2,
        hasQuiz = true,
        quizQuestions = listOf(
            QuizQuestion(
                questionId = "q1",
                question = "What makes a strong password?",
                options = listOf(
                    "Mix of letters, numbers, and symbols",
                    "Your pet's name",
                    "123456",
                    "password"
                ),
                correctAnswer = 0,
                explanation = "Strong passwords combine different character types for better security."
            )
        )
    )
)

private val sampleProgress = listOf(
    UserProgress(
        progressId = "1",
        userId = "user1",
        lessonId = "lesson_smartphone_basics",
        completed = false,
        score = 0,
        timeSpent = 0
    )
)