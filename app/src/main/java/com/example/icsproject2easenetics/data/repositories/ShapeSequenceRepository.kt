package com.example.icsproject2easenetics.data.repositories

import com.example.icsproject2easenetics.data.models.ShapeSequenceLevel
import javax.inject.Inject

class ShapeSequenceRepository @Inject constructor() {

    // Available shapes for the game
    private val allShapes = listOf(
        "🔺", "🔷", "🔴", "⭐", "⬛", "💠", "🔶",
        "🟢", "🔵", "🟡", "🟣", "🟤", "⚪", "⚫"
    )

    fun getGameLevels(): List<ShapeSequenceLevel> {
        return listOf(
            ShapeSequenceLevel(
                level = 1,
                title = "Beginner",
                description = "Remember 3 shapes with plenty of time",
                flashDuration = 3000L,
                sequenceLength = 3,
                shapes = allShapes.subList(0, 7),
                requiredScore = 70,
                isLocked = false
            ),
            ShapeSequenceLevel(
                level = 2,
                title = "Easy",
                description = "Remember 4 shapes with good viewing time",
                flashDuration = 2500L,
                sequenceLength = 4,
                shapes = allShapes.subList(0, 7),
                requiredScore = 75,
                isLocked = false
            ),
            ShapeSequenceLevel(
                level = 3,
                title = "Medium",
                description = "Remember 5 shapes with moderate time",
                flashDuration = 2000L,
                sequenceLength = 5,
                shapes = allShapes.subList(0, 7),
                requiredScore = 80,
                isLocked = true
            ),
            ShapeSequenceLevel(
                level = 4,
                title = "Challenging",
                description = "Remember 5 shapes with less time",
                flashDuration = 1500L,
                sequenceLength = 5,
                shapes = allShapes.subList(0, 7),
                requiredScore = 85,
                isLocked = true
            ),
            ShapeSequenceLevel(
                level = 5,
                title = "Hard",
                description = "Remember 6 shapes with brief display",
                flashDuration = 1200L,
                sequenceLength = 6,
                shapes = allShapes,
                requiredScore = 90,
                isLocked = true
            ),
            ShapeSequenceLevel(
                level = 6,
                title = "Advanced",
                description = "Remember 6 shapes with very brief display",
                flashDuration = 800L,
                sequenceLength = 6,
                shapes = allShapes,
                requiredScore = 95,
                isLocked = true
            ),
            ShapeSequenceLevel(
                level = 7,
                title = "Expert",
                description = "Remember 7 shapes with flash display",
                flashDuration = 500L,
                sequenceLength = 7,
                shapes = allShapes,
                requiredScore = 100,
                isLocked = true
            )
        )
    }

    fun generateSequence(level: ShapeSequenceLevel): List<String> {
        return level.shapes.shuffled().take(level.sequenceLength)
    }

    fun calculateScore(
        correctSequence: List<String>,
        userSequence: List<String>,
        timeTaken: Long,
        level: ShapeSequenceLevel
    ): Int {
        if (correctSequence.size != userSequence.size) return 0

        var correctCount = 0
        for (i in correctSequence.indices) {
            if (correctSequence[i] == userSequence[i]) {
                correctCount++
            }
        }

        val accuracy = (correctCount.toDouble() / correctSequence.size) * 100

        // Time bonus: faster completion gives more points
        val maxTimeBonus = 20.0
        val timeBonus = maxOf(0.0, (level.flashDuration - timeTaken) / level.flashDuration.toDouble() * maxTimeBonus)

        return (accuracy + timeBonus).toInt().coerceAtMost(100)
    }

    fun shouldUnlockNextLevel(currentLevel: Int, score: Int, requiredScore: Int): Boolean {
        return score >= requiredScore
    }
}