package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.ui.components.AccessibleButton
import com.example.icsproject2easenetics.utils.AccessibilityManager
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel
import com.example.icsproject2easenetics.ui.viewmodels.LessonViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ProgressViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lessonId: String,
    questions: List<QuizQuestion>,
    onQuizComplete: (Int, Int) -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    lessonViewModel: LessonViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableIntStateOf(-1) }
    var userAnswers by remember { mutableStateOf<List<Int>>(emptyList()) }
    var showResults by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isSavingProgress by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

// In your QuizScreen.kt, update the LaunchedEffect:
    LaunchedEffect(showResults) {
        if (showResults && score > 0 && currentUser != null && !isSavingProgress) {
            isSavingProgress = true

            // Calculate final score
            val finalScore = (score.toDouble() / questions.size * 100).toInt()
            println("🎯 QuizScreen: Quiz completed! Score: $score/${questions.size} ($finalScore%)")

            try {
                // Update progress in Firebase through UserViewModel
                currentUser?.uid?.let { userId ->
                    println("🔄 QuizScreen: Saving progress for user $userId, lesson $lessonId")

                    // Use a coroutine scope that won't be cancelled
                    userViewModel.updateQuizScore(userId, lessonId, finalScore)

                    // Wait a bit for the save to complete
                    delay(1000) // Give it time to save

                    // Also update in LessonViewModel for immediate UI updates
                    lessonViewModel.updateQuizScore(score, questions.size)

                    // Refresh progress data to update achievements
                    progressViewModel.loadProgressData(userId)

                    println("✅ QuizScreen: Progress saved successfully")
                }
            } catch (e: Exception) {
                println("❌ QuizScreen: Error saving progress: ${e.message}")
            } finally {
                // Call completion callback regardless of save status
                onQuizComplete(score, questions.size)
                isSavingProgress = false
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Quiz",
                        style = AccessibilityManager.getScaledTitleLarge(),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showResults) {
                                onBack()
                            } else {
                                // Show confirmation dialog or just go back
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (questions.isEmpty()) {
            // No questions available
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Quiz,
                    contentDescription = "No Quiz",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Quiz Available",
                    style = AccessibilityManager.getScaledTitleLarge(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This lesson doesn't have a quiz yet, or the questions couldn't be loaded.",
                    style = AccessibilityManager.getScaledBodyMedium(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                AccessibleButton(onClick = onBack) {
                    Text("Back to Lesson")
                }
            }
        } else if (showResults) {
            // Quiz Results Screen
            QuizResultsScreen(
                score = score,
                totalQuestions = questions.size,
                onBackToLesson = onBack,
                isSaving = isSavingProgress,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            // Active Quiz Screen
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Progress Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                style = AccessibilityManager.getScaledBodyMedium(),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${((currentQuestionIndex + 1).toDouble() / questions.size * 100).toInt()}%",
                                style = AccessibilityManager.getScaledBodyMedium(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = (currentQuestionIndex + 1).toFloat() / questions.size.toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Question Content
                if (currentQuestion != null) {
                    QuestionContent(
                        question = currentQuestion,
                        selectedAnswer = selectedAnswer,
                        onAnswerSelected = { selectedAnswer = it },
                        onNext = {
                            // Save answer
                            val newAnswers = userAnswers.toMutableList()
                            if (newAnswers.size > currentQuestionIndex) {
                                newAnswers[currentQuestionIndex] = selectedAnswer
                            } else {
                                newAnswers.add(selectedAnswer)
                            }
                            userAnswers = newAnswers

                            // Check if answer is correct
                            if (selectedAnswer == currentQuestion.correctAnswer) {
                                score++
                            }

                            // Move to next question or show results
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                selectedAnswer = userAnswers.getOrNull(currentQuestionIndex) ?: -1
                            } else {
                                showResults = true
                            }
                        },
                        isLastQuestion = currentQuestionIndex == questions.size - 1,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    // Error state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading question...",
                            style = AccessibilityManager.getScaledBodyMedium()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionContent(
    question: QuizQuestion,
    selectedAnswer: Int,
    onAnswerSelected: (Int) -> Unit,
    onNext: () -> Unit,
    isLastQuestion: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Question",
                    style = AccessibilityManager.getScaledBodySmall(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.question,
                    style = AccessibilityManager.getScaledTitleMedium(),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Select your answer:",
                    style = AccessibilityManager.getScaledBodyMedium(),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                question.options.forEachIndexed { index, option ->
                    AnswerOption(
                        text = option,
                        isSelected = selectedAnswer == index,
                        onClick = { onAnswerSelected(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        // Next Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            AccessibleButton(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswer != -1
            ) {
                Text(
                    text = if (isLastQuestion) "Finish Quiz" else "Next Question",
                    style = AccessibilityManager.getScaledBodyMedium()
                )
            }
        }
    }
}

@Composable
fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = text,
                style = AccessibilityManager.getScaledBodyMedium(),
                modifier = Modifier.weight(1f),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun QuizResultsScreen(
    score: Int,
    totalQuestions: Int,
    onBackToLesson: () -> Unit,
    isSaving: Boolean = false,
    modifier: Modifier = Modifier
) {
    val percentage = (score.toDouble() / totalQuestions * 100).toInt()
    val isPassing = percentage >= 70

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Results Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isPassing) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Saving your progress...",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Text(
                        text = if (isPassing) "🎉 Quiz Complete!" else "📝 Quiz Complete",
                        style = AccessibilityManager.getScaledTitleLarge(),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Score Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = percentage.toFloat() / 100f,
                            modifier = Modifier.size(120.dp),
                            color = if (isPassing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                            strokeWidth = 8.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isPassing) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "$score/$totalQuestions",
                                style = AccessibilityManager.getScaledBodySmall(),
                                color = if (isPassing) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isPassing) "Great job! You've passed the quiz."
                        else "Good effort! Review the material and try again.",
                        style = AccessibilityManager.getScaledBodyMedium(),
                        textAlign = TextAlign.Center,
                        color = if (isPassing) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        if (!isSaving) {
            // Performance Feedback
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Performance Summary",
                        style = AccessibilityManager.getScaledTitleMedium(),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    PerformanceMetric(
                        label = "Correct Answers",
                        value = "$score out of $totalQuestions",
                        color = MaterialTheme.colorScheme.primary
                    )

                    PerformanceMetric(
                        label = "Success Rate",
                        value = "$percentage%",
                        color = if (isPassing) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )

                    PerformanceMetric(
                        label = "Passing Grade",
                        value = if (isPassing) "Achieved ✓" else "Not Achieved",
                        color = if (isPassing) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }

            // Progress Saved Notice
            if (isPassing) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Circle,
                            contentDescription = "Saved",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = "Your quiz score has been saved to your progress",
                            style = AccessibilityManager.getScaledBodySmall(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AccessibleButton(
                    onClick = onBackToLesson,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Back to Lesson",
                        style = AccessibilityManager.getScaledBodyMedium()
                    )
                }

                if (!isPassing) {
                    Button(
                        onClick = { /* TODO: Implement retry quiz */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }

            // Encouragement Message
            Text(
                text = getQuizEncouragement(percentage),
                style = AccessibilityManager.getScaledBodyMedium(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun PerformanceMetric(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AccessibilityManager.getScaledBodyMedium(),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = AccessibilityManager.getScaledBodyMedium(),
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun getQuizEncouragement(percentage: Int): String {
    return when {
        percentage >= 90 -> "Outstanding! You've mastered this material. 🎯"
        percentage >= 80 -> "Excellent work! You have a strong understanding. 💪"
        percentage >= 70 -> "Good job! You've passed the quiz. 👍"
        percentage >= 60 -> "Almost there! Review the material and try again. 📚"
        else -> "Keep learning! Review the lesson and try the quiz again. 🌟"
    }
}