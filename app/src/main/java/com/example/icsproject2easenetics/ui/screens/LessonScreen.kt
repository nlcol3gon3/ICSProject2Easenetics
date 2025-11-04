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
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.UserProgress
import com.example.icsproject2easenetics.data.models.DifficultyLevel
import com.example.icsproject2easenetics.data.models.LessonCategory
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.service.VoiceService
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel

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

    var isNarrating by remember { mutableStateOf(false) }
    val isSpeaking by voiceService.isSpeaking.collectAsState()
    val speakingError by voiceService.speakingError.collectAsState()

    // Use sample data instead
    val currentLesson = sampleLessons.find { it.lessonId == lessonId }
    val lessonProgress = sampleProgress.find { it.lessonId == lessonId }

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
                    // Voice Narration Toggle - NEW
                    if (currentLesson != null) {
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    voiceService.stopSpeaking()
                                    isNarrating = false
                                } else {
                                    val narrationText = """
                                        ${currentLesson.title}
                                        
                                        ${currentLesson.description}
                                        
                                        ${currentLesson.content}
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
                    isCompleted = lessonProgress?.completed ?: false,
                    hasQuiz = currentLesson.hasQuiz,
                    onMarkComplete = {
                        voiceService.stopSpeaking()
                        onMarkComplete()
                    },
                    onStartQuiz = {
                        voiceService.stopSpeaking()
                        if (currentLesson.quizQuestions.isNotEmpty()) {
                            onStartQuiz(currentLesson.lessonId, currentLesson.quizQuestions)
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
                    colors = androidx.compose.material3.CardDefaults.cardColors(
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
                Text(
                    text = "Lesson not found",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LessonContent(
                    lesson = currentLesson,
                    progress = lessonProgress,
                    voiceService = voiceService, // NEW
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
    voiceService: VoiceService, // NEW
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        if (progress != null) {
            Column {
                Text(
                    text = "Your Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (progress.completed) 1.0f else 0.5f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Lesson info card
        Card(
            modifier = Modifier.fillMaxWidth()
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
            modifier = Modifier.fillMaxWidth()
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

                    // Read content aloud button - NEW
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
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                )
            }
        }

        // Video section (if available)
        lesson.videoUrl?.let { videoUrl ->
            Card(
                modifier = Modifier.fillMaxWidth()
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

                    // Video placeholder
                    Button(
                        onClick = { /* Play video */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.PlayArrow, "Play Video")
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Watch Video Tutorial")
                    }
                }
            }
        }
    }
}


@Composable
fun LessonMetadata(lesson: Lesson) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MetadataRow("Duration", "${lesson.duration} minutes")
        MetadataRow("Difficulty", lesson.difficulty.name)
        MetadataRow("Category", lesson.category.name)
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
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
                    Text("Lesson Completed!", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (hasQuiz) {
                        Button(onClick = onStartQuiz) {
                            Text("Take Quiz")
                        }
                    }
                    Button(onClick = onMarkComplete) {
                        Text("Mark Complete")
                    }
                }
            }
        }
    }
}

// Sample data for testing
private val sampleLessons = listOf(
    Lesson(
        lessonId = "lesson_smartphone_basics",
        title = "Getting Started with Your Smartphone",
        description = "Learn the basics of using your smartphone",
        content = "In this lesson, you'll learn how to:\n\n• Turn your smartphone on and off\n• Make and receive calls\n• Send text messages\n• Take photos and videos\n• Connect to Wi-Fi\n\nA smartphone is like a small computer that fits in your pocket. It can help you stay connected with family, access information, and even have fun!",
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
                explanation = "Smartphones are designed for communication, internet access, and many other useful functions."
            ),
            QuizQuestion(
                questionId = "q2",
                question = "How do you turn on a smartphone?",
                options = listOf(
                    "Press and hold the power button",
                    "Shake the phone vigorously",
                    "Shout 'turn on' at the phone",
                    "Put it in sunlight"
                ),
                correctAnswer = 0,
                explanation = "The power button is usually on the side or top of the phone. Press and hold it for a few seconds."
            ),
            QuizQuestion(
                questionId = "q3",
                question = "What does Wi-Fi allow you to do?",
                options = listOf(
                    "Connect to the internet without using mobile data",
                    "Make phone calls for free",
                    "Charge your phone wirelessly",
                    "Improve camera quality"
                ),
                correctAnswer = 0,
                explanation = "Wi-Fi lets you access the internet through wireless networks, saving your mobile data."
            )
        )
    ),
    Lesson(
        lessonId = "lesson_internet_safety",
        title = "Safe Internet Browsing",
        description = "Stay safe while browsing the internet",
        content = "Internet safety is important for everyone. In this lesson, you'll learn:\n\n• How to identify safe websites\n• Creating strong passwords\n• Recognizing email scams\n• Protecting personal information\n• Using antivirus software\n\nRemember: Never share personal information like your Social Security number or bank details with strangers online.",
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
                explanation = "Strong passwords combine uppercase, lowercase, numbers, and symbols for better security."
            ),
            QuizQuestion(
                questionId = "q2",
                question = "What should you do if you receive a suspicious email?",
                options = listOf(
                    "Delete it without clicking links",
                    "Click all links to see what happens",
                    "Reply with your personal information",
                    "Forward it to all your contacts"
                ),
                correctAnswer = 0,
                explanation = "Suspicious emails often contain phishing attempts. It's safest to delete them."
            )
        )
    ),
    Lesson(
        lessonId = "lesson_social_media",
        title = "Connecting with Family on Social Media",
        description = "Connect with loved ones on social platforms",
        content = "Social media can help you stay connected with family and friends. In this lesson:\n\n• Creating a Facebook account\n• Finding and adding friends\n• Sharing photos and updates\n• Sending private messages\n• Privacy settings explained\n\nSocial media is a great way to see what your grandchildren are up to!",
        duration = 25,
        difficulty = DifficultyLevel.INTERMEDIATE,
        category = LessonCategory.SOCIAL_MEDIA,
        order = 3,
        hasQuiz = true,
        quizQuestions = listOf(
            QuizQuestion(
                questionId = "q1",
                question = "What is the main benefit of social media for older adults?",
                options = listOf(
                    "Staying connected with family and friends",
                    "Getting discounts on shopping",
                    "Watching unlimited videos",
                    "Playing games all day"
                ),
                correctAnswer = 0,
                explanation = "Social media helps maintain relationships with loved ones, especially those who live far away."
            )
        )
    ),
    Lesson(
        lessonId = "lesson_online_safety",
        title = "Online Safety Basics",
        description = "Protect yourself from online threats",
        content = "Stay safe online with these essential tips:\n\n• Recognizing phishing emails\n• Safe online shopping practices\n• Protecting your identity\n• Using secure websites (look for https://)\n• What to do if you suspect fraud\n\nAlways remember: If something seems too good to be true, it probably is!",
        duration = 18,
        difficulty = DifficultyLevel.BEGINNER,
        category = LessonCategory.ONLINE_SAFETY,
        order = 4,
        hasQuiz = false
    ),
    Lesson(
        lessonId = "lesson_video_calls",
        title = "Using Video Calls",
        description = "Make video calls to family and friends",
        content = "Video calls let you see and talk to loved ones face-to-face. Learn how to:\n\n• Set up video calling apps (Zoom, FaceTime, WhatsApp)\n• Make and receive video calls\n• Adjust camera and microphone settings\n• Share your screen\n• Troubleshoot common issues\n\nVideo calls are perfect for virtual family gatherings!",
        duration = 22,
        difficulty = DifficultyLevel.INTERMEDIATE,
        category = LessonCategory.COMMUNICATION,
        order = 5,
        hasQuiz = true,
        quizQuestions = listOf(
            QuizQuestion(
                questionId = "q1",
                question = "Which app is commonly used for video calls?",
                options = listOf(
                    "All of the above",
                    "Zoom",
                    "FaceTime",
                    "WhatsApp"
                ),
                correctAnswer = 0,
                explanation = "Many apps support video calls, including Zoom, FaceTime, WhatsApp, and others."
            )
        )
    )
)

private val sampleProgress = listOf(
    UserProgress(
        progressId = "1",
        userId = "user1",
        lessonId = "lesson_smartphone_basics",
        completed = true,
        score = 85,
        timeSpent = 900
    ),
    UserProgress(
        progressId = "2",
        userId = "user1",
        lessonId = "lesson_internet_safety",
        completed = false,
        score = 0,
        timeSpent = 300
    ),
    UserProgress(
        progressId = "3",
        userId = "user1",
        lessonId = "lesson_social_media",
        completed = true,
        score = 90,
        timeSpent = 1200
    ),
    UserProgress(
        progressId = "4",
        userId = "user1",
        lessonId = "lesson_online_safety",
        completed = false,
        score = 0,
        timeSpent = 150
    ),
    UserProgress(
        progressId = "5",
        userId = "user1",
        lessonId = "lesson_video_calls",
        completed = false,
        score = 0,
        timeSpent = 0
    )
)