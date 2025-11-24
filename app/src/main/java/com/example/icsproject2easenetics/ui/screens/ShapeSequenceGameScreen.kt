package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.icsproject2easenetics.data.models.ShapeSequenceLevel
import com.example.icsproject2easenetics.ui.viewmodels.ShapeSequenceViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ShapeSequenceViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShapeSequenceGameScreen(
    onBack: () -> Unit
) {
    // Use custom factory to avoid dependency injection issues
    val viewModel: ShapeSequenceViewModel = viewModel(
        factory = ShapeSequenceViewModelFactory()
    )

    val levels by viewModel.levels.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val currentSequence by viewModel.currentSequence.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val score by viewModel.score.collectAsState()
    val userProgress by viewModel.userProgress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shape Sequence Memory",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                // Loading state
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
                        text = "Loading game...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (gameState) {
                        is ShapeSequenceViewModel.GameState.IDLE -> {
                            LevelSelectionSection(
                                levels = levels,
                                currentLevel = currentLevel,
                                userProgress = userProgress,
                                onLevelSelected = { viewModel.setCurrentLevel(it) },
                                onStartGame = { viewModel.startNewSequence() }
                            )
                        }
                        is ShapeSequenceViewModel.GameState.SHOWING_SEQUENCE -> {
                            SequenceDisplaySection(
                                sequence = currentSequence?.sequence ?: emptyList(),
                                isShowing = currentSequence?.isShowing == true
                            )
                        }
                        is ShapeSequenceViewModel.GameState.USER_INPUT -> {
                            UserInputSection(
                                currentLevel = currentLevel,
                                userSequence = currentSequence?.userSequence ?: emptyList(),
                                onShapeSelected = { viewModel.addUserShape(it) },
                                onClear = { viewModel.clearUserSequence() }
                            )
                        }
                        is ShapeSequenceViewModel.GameState.RESULT -> {
                            ResultSection(
                                score = score,
                                requiredScore = currentLevel?.requiredScore ?: 0,
                                onContinue = { viewModel.resetGame() },
                                onRetry = { viewModel.retryLevel() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelSelectionSection(
    levels: List<ShapeSequenceLevel>,
    currentLevel: ShapeSequenceLevel?,
    userProgress: List<Int>,
    onLevelSelected: (ShapeSequenceLevel) -> Unit,
    onStartGame: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current Level Info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentLevel?.title ?: "Select Level",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentLevel?.description ?: "Choose a difficulty level to start",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Flash Duration: ${currentLevel?.flashDuration?.div(1000f) ?: 0f} seconds",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Sequence Length: ${currentLevel?.sequenceLength ?: 0} shapes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (currentLevel != null) {
                    Text(
                        text = "Required Score: ${currentLevel.requiredScore}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Level Grid
        Text(
            text = "Select Difficulty Level",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { level ->
                LevelCard(
                    level = level,
                    isCompleted = userProgress.contains(level.level),
                    isSelected = currentLevel?.level == level.level,
                    onClick = { onLevelSelected(level) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start Button
        Button(
            onClick = onStartGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            enabled = currentLevel != null && !currentLevel.isLocked
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Start")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (currentLevel?.isLocked == true) "Level Locked" else "Start Game",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun LevelCard(
    level: ShapeSequenceLevel,
    isCompleted: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !level.isLocked,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                level.isLocked -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = level.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        level.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${level.sequenceLength} shapes • ${level.flashDuration.div(1000f)}s display",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        level.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = "Required: ${level.requiredScore}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        level.isLocked -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            if (level.isLocked) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (isCompleted) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SequenceDisplaySection(
    sequence: List<String>,
    isShowing: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isShowing) "Memorize the sequence..." else "Get ready...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Progress indicator for sequence display
        if (isShowing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sequence.forEachIndexed { index, shape ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isShowing) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isShowing) {
                        Text(
                            text = shape,
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 32.sp
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (!isShowing) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Now recreate the sequence!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun UserInputSection(
    currentLevel: ShapeSequenceLevel?,
    userSequence: List<String>,
    onShapeSelected: (String) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructions
        Text(
            text = "Recreate the sequence you saw",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Tap the shapes in the correct order",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // User's current sequence
        Text(
            text = "Your sequence:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Display user's current attempt
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            val targetLength = currentLevel?.sequenceLength ?: 0
            repeat(targetLength) { index ->
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < userSequence.size) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < userSequence.size) {
                        Text(
                            text = userSequence[index],
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 24.sp
                        )
                    } else {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Progress indicator
        Text(
            text = "${userSequence.size}/${currentLevel?.sequenceLength ?: 0}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Available shapes grid
        Text(
            text = "Available Shapes:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val availableShapes = currentLevel?.shapes ?: emptyList()
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableShapes) { shape ->
                ShapeButton(
                    shape = shape,
                    onClick = { onShapeSelected(shape) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                enabled = userSequence.isNotEmpty()
            ) {
                Text("Clear")
            }

            Button(
                onClick = { /* Optional: Add submit functionality if needed */ },
                modifier = Modifier.weight(1f),
                enabled = false // Disabled since we auto-submit when sequence is complete
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun ShapeButton(
    shape: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shape,
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 32.sp
            )
        }
    }
}

@Composable
fun ResultSection(
    score: Int,
    requiredScore: Int,
    onContinue: () -> Unit,
    onRetry: () -> Unit
) {
    val isSuccess = score >= requiredScore

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Result icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (isSuccess) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSuccess) "🎉" else "💡",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Result text
        Text(
            text = if (isSuccess) "Excellent!" else "Good Try!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSuccess) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Score display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$score%",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Required: $requiredScore%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Success indicator
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isSuccess) "✓ Level Completed!" else "✗ Try Again",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSuccess) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Feedback message
        Text(
            text = if (isSuccess) {
                "You've unlocked the next level! Your visual memory is impressive."
            } else {
                "Keep practicing! Try to focus on the sequence order and timing."
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onContinue,
                modifier = Modifier.weight(1f)
            ) {
                Text("Select Level")
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSuccess) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            ) {
                Text(if (isSuccess) "Next Level" else "Try Again")
            }
        }

        // Additional info for success
        if (isSuccess) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your progress is saved",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}