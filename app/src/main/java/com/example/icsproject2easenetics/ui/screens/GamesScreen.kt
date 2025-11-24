package com.example.icsproject2easenetics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.icsproject2easenetics.data.models.Game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onBack: () -> Unit,
    onGameSelected: (Game) -> Unit
) {
    val availableGames = getAvailableGames()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Brain Games",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Boost Your Cognitive Skills",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fun games designed to improve memory, attention, and cognitive flexibility",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Games List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableGames) { game ->
                    GameCard(
                        game = game,
                        onPlayClick = { onGameSelected(game) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Game Icon and Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Game Icon
                Card(
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = game.iconRes,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }

                // Game Details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = game.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Benefits
                    Text(
                        text = "Benefits: ${game.benefits.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    // Difficulty Levels
                    Text(
                        text = "Levels: ${game.difficultyLevels.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Play Button
            IconButton(
                onClick = onPlayClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play ${game.title}",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Function to get available games
private fun getAvailableGames(): List<Game> {
    return listOf(
        Game(
            id = "shape_sequence",
            title = "Shape Sequence Memory",
            description = "Memorize and recreate sequences of geometric shapes to boost visual memory",
            iconRes = "🔺",
            difficultyLevels = listOf("Beginner", "Easy", "Medium", "Challenging", "Hard", "Advanced", "Expert"),
            benefits = listOf("Visual Memory", "Working Memory", "Attention Span", "Pattern Recognition"),
            targetSkills = listOf("Memory Recall", "Focus", "Cognitive Speed")
        ),
//        Game(
//            id = "memory_cards",
//            title = "Memory Match",
//            description = "Match pairs of cards featuring Kenyan landmarks and historical figures",
//            iconRes = "🎴",
//            difficultyLevels = listOf("Easy", "Medium", "Hard"),
//            benefits = listOf("Memory Recall", "Concentration", "Visual Recognition"),
//            targetSkills = listOf("Short-term Memory", "Focus", "Visual Processing")
//        ),
//        Game(
//            id = "word_pairs",
//            title = "Word Association",
//            description = "Match related words to enhance vocabulary and cognitive associations",
//            iconRes = "📝",
//            difficultyLevels = listOf("Easy", "Medium", "Hard"),
//            benefits = listOf("Vocabulary", "Associative Memory", "Cognitive Flexibility"),
//            targetSkills = listOf("Verbal Memory", "Pattern Recognition", "Mental Flexibility")
//        )
    )
}