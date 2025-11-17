@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
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
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.ui.viewmodels.LessonViewModel
import com.example.icsproject2easenetics.extensions.cleanMarkdown


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lessonId: String,
    questions: List<QuizQuestion>,
    onQuizComplete: (score: Int, total: Int) -> Unit,
    onBack: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var showResults by remember { mutableStateOf(false) }
    var quizSubmitted by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val userAnswer = userAnswers[currentQuestion?.questionId]

    // Calculate progress
    val progress = if (questions.isNotEmpty()) {
        (currentQuestionIndex + 1).toFloat() / questions.size
    } else {
        0f
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (showResults) "Quiz Results".cleanMarkdown() else "Lesson Quiz".cleanMarkdown(),
                        style = MaterialTheme.typography.headlineMedium,
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (showResults) {
                QuizResults(
                    questions = questions,
                    userAnswers = userAnswers,
                    quizSubmitted = quizSubmitted,
                    onComplete = { score, total ->
                        quizSubmitted = true
                        onQuizComplete(score, total)
                    },
                    onRetry = {
                        // Reset quiz
                        currentQuestionIndex = 0
                        userAnswers = emptyMap()
                        showResults = false
                        quizSubmitted = false
                    }
                )
            } else if (currentQuestion == null) {
                // No questions available
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "No questions",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No questions available".cleanMarkdown(),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This lesson doesn't have any quiz questions yet.".cleanMarkdown(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onBack) {
                        Text("Return to Lesson".cleanMarkdown())
                    }
                }
            } else {
                // Quiz in progress
                QuizInProgress(
                    currentQuestion = currentQuestion,
                    currentQuestionIndex = currentQuestionIndex,
                    totalQuestions = questions.size,
                    progress = progress,
                    userAnswer = userAnswer,
                    onAnswerSelected = { answerIndex ->
                        userAnswers = userAnswers + (currentQuestion.questionId to answerIndex)
                    },
                    onPrevious = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                        }
                    },
                    onNext = {
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        } else {
                            showResults = true
                        }
                    },
                    canGoBack = currentQuestionIndex > 0,
                    canGoForward = userAnswer != null,
                    isLastQuestion = currentQuestionIndex == questions.size - 1
                )
            }
        }
    }
}

@Composable
fun QuizInProgress(
    currentQuestion: QuizQuestion,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    progress: Float,
    userAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    canGoBack: Boolean,
    canGoForward: Boolean,
    isLastQuestion: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of $totalQuestions".cleanMarkdown(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%".cleanMarkdown(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = currentQuestion.question.cleanMarkdown(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    lineHeight = MaterialTheme.typography.titleLarge.lineHeight * 1.1
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Options
                currentQuestion.options.forEachIndexed { index, option ->
                    QuizOption(
                        option = option.cleanMarkdown(),
                        index = index,
                        isSelected = userAnswer == index,
                        onClick = { onAnswerSelected(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onPrevious,
                enabled = canGoBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(if (canGoBack) "Previous".cleanMarkdown() else "Cancel".cleanMarkdown())
            }

            Button(
                onClick = onNext,
                enabled = canGoForward,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isLastQuestion) "Finish Quiz".cleanMarkdown() else "Next Question".cleanMarkdown())
            }
        }
    }
}

@Composable
fun QuizOption(
    option: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val optionLetters = listOf("A", "B", "C", "D")

    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option letter indicator
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = optionLetters.getOrElse(index) { "" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Option text
            Text(
                text = option,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            // Selection indicator
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@Composable
fun QuizResults(
    questions: List<QuizQuestion>,
    userAnswers: Map<String, Int>,
    quizSubmitted: Boolean,
    onComplete: (score: Int, total: Int) -> Unit,
    onRetry: () -> Unit
) {
    val score = questions.count { question ->
        userAnswers[question.questionId] == question.correctAnswer
    }
    val total = questions.size
    val percentage = if (total > 0) (score.toDouble() / total * 100).toInt() else 0

    // Calculate performance rating
    val performance = when {
        percentage >= 90 -> "Excellent! 🎉"
        percentage >= 75 -> "Great Job! 👍"
        percentage >= 60 -> "Good Work! 👏"
        else -> "Keep Practicing! 💪"
    }

    // Trigger completion callback
    LaunchedEffect(Unit) {
        if (!quizSubmitted) {
            onComplete(score, total)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Results summary card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Result icon based on performance
                Icon(
                    imageVector = if (percentage >= 70) Icons.Filled.ThumbUp else Icons.Filled.Star,
                    contentDescription = "Quiz Result",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quiz Complete!".cleanMarkdown(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = performance.cleanMarkdown(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Your Score".cleanMarkdown(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "$score/$total".cleanMarkdown(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$percentage%".cleanMarkdown(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Performance message
                Text(
                    text = when {
                        percentage == 100 -> "Perfect score! You've mastered this topic! 🌟"
                        percentage >= 80 -> "Outstanding! You have a great understanding of this material."
                        percentage >= 60 -> "Good work! You understand the main concepts well."
                        else -> "Don't worry! Review the material and try again. Learning takes practice!"
                    }.cleanMarkdown(),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Detailed review section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "📋 Question Review".cleanMarkdown(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    questions.forEachIndexed { index, question ->
                        QuestionReviewItem(
                            question = question,
                            questionNumber = index + 1,
                            userAnswer = userAnswers[question.questionId],
                            isCorrect = userAnswers[question.questionId] == question.correctAnswer
                        )
                    }
                }
            }
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Try Again".cleanMarkdown())
            }

            Button(
                onClick = { /* Navigate back to lesson */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Back to Lesson".cleanMarkdown())
            }
        }
    }
}

@Composable
fun QuestionReviewItem(
    question: QuizQuestion,
    questionNumber: Int,
    userAnswer: Int?,
    isCorrect: Boolean
) {
    val optionLetters = listOf("A", "B", "C", "D")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Question header with result indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Correct/incorrect indicator
                Icon(
                    imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (isCorrect) "Correct" else "Incorrect",
                    tint = if (isCorrect) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Question $questionNumber".cleanMarkdown(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Question text
            Text(
                text = question.question.cleanMarkdown(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // User's answer
            Column {
                Text(
                    text = "Your answer:".cleanMarkdown(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val userAnswerText = if (userAnswer != null) {
                    "${optionLetters[userAnswer]}. ${question.options[userAnswer].cleanMarkdown()}"
                } else {
                    "Not answered"
                }

                Text(
                    text = userAnswerText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isCorrect) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }

            // Correct answer (if incorrect)
            if (!isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text(
                        text = "Correct answer:".cleanMarkdown(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${optionLetters[question.correctAnswer]}. ${question.options[question.correctAnswer].cleanMarkdown()}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Explanation (if available)
            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "💡 ${question.explanation.cleanMarkdown()}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}