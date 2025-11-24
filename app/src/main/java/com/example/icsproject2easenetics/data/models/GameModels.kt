package com.example.icsproject2easenetics.data.models

data class Game(
    val id: String,
    val title: String,
    val description: String,
    val iconRes: String,
    val difficultyLevels: List<String>,
    val benefits: List<String>,
    val targetSkills: List<String>
)

data class ShapeSequenceLevel(
    val level: Int,
    val title: String,
    val description: String,
    val flashDuration: Long, // milliseconds the shapes are shown
    val sequenceLength: Int, // number of shapes in sequence
    val shapes: List<String>, // available shapes for this level
    val requiredScore: Int,
    val isLocked: Boolean = false
)

data class ShapeSequence(
    val sequence: List<String>, // list of shape emojis in order
    val userSequence: List<String> = emptyList(), // user's current attempt
    val isShowing: Boolean = false // whether sequence is currently being shown
)

data class ShapeGameProgress(
    val currentLevel: Int,
    val totalScore: Int,
    val completedLevels: List<Int>,
    val bestScores: Map<Int, Int> // level to best score
)

data class UserGameProgress(
    val userId: String = "",
    val gameId: String = "",
    val completedLevels: List<Int> = emptyList(),
    val bestScores: Map<Int, Int> = emptyMap(), // level to best score
    val totalGamesPlayed: Int = 0,
    val lastPlayed: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "gameId" to gameId,
            "completedLevels" to completedLevels,
            "bestScores" to bestScores,
            "totalGamesPlayed" to totalGamesPlayed,
            "lastPlayed" to lastPlayed
        )
    }
}