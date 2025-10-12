// Create new file: QuizScreen.kt
package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.icsproject2easenetics.data.models.QuizQuestion

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

    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val userAnswer = userAnswers[currentQuestion?.questionId]

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Lesson Quiz",
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
                    onComplete = { score, total ->
                        onQuizComplete(score, total)
                    }
                )
            } else if (currentQuestion == null) {
                Text("No questions available", textAlign = TextAlign.Center)
            } else {
                // Progress indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Question ${currentQuestionIndex + 1} of ${questions.size}")
                    Text("${((currentQuestionIndex + 1) / questions.size.toFloat() * 100).toInt()}%")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question card
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options
                        currentQuestion.options.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = userAnswer == index,
                                    onClick = {
                                        userAnswers = userAnswers +
                                                (currentQuestion.questionId to index)
                                    }
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
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
                        onClick = {
                            if (currentQuestionIndex > 0) {
                                currentQuestionIndex--
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Text(if (currentQuestionIndex > 0) "Previous" else "Cancel")
                    }

                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                showResults = true
                            }
                        },
                        enabled = userAnswer != null
                    ) {
                        Text(if (currentQuestionIndex < questions.size - 1) "Next" else "Finish")
                    }
                }
            }
        }
    }
}

@Composable
fun QuizResults(
    questions: List<QuizQuestion>,
    userAnswers: Map<String, Int>,
    onComplete: (score: Int, total: Int) -> Unit
) {
    val score = questions.count { question ->
        userAnswers[question.questionId] == question.correctAnswer
    }
    val total = questions.size

    LaunchedEffect(Unit) {
        onComplete(score, total)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Quiz Complete",
                modifier = Modifier.padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Quiz Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You scored $score out of $total",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${((score.toDouble() / total) * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question review
            Text(
                text = "Review Answers:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                questions.forEachIndexed { index, question ->
                    val userAnswer = userAnswers[question.questionId]
                    val isCorrect = userAnswer == question.correctAnswer

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${index + 1}. ${question.question}",
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Your answer: ${question.options.getOrNull(userAnswer ?: -1) ?: "Not answered"}",
                                color = if (isCorrect) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )

                            if (!isCorrect) {
                                Text(
                                    text = "Correct answer: ${question.options[question.correctAnswer]}",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            if (question.explanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "💡 ${question.explanation}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}