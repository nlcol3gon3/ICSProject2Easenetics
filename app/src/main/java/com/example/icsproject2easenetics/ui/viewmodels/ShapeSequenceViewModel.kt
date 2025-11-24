package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.ShapeSequence
import com.example.icsproject2easenetics.data.models.ShapeSequenceLevel
import com.example.icsproject2easenetics.data.repositories.GameProgressRepository
import com.example.icsproject2easenetics.data.repositories.ShapeSequenceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShapeSequenceViewModel @Inject constructor(
    private val shapeSequenceRepository: ShapeSequenceRepository,
    private val gameProgressRepository: GameProgressRepository
) : ViewModel() {

    private val _levels = MutableStateFlow<List<ShapeSequenceLevel>>(emptyList())
    val levels: StateFlow<List<ShapeSequenceLevel>> get() = _levels

    private val _currentLevel = MutableStateFlow<ShapeSequenceLevel?>(null)
    val currentLevel: StateFlow<ShapeSequenceLevel?> get() = _currentLevel

    private val _currentSequence = MutableStateFlow<ShapeSequence?>(null)
    val currentSequence: StateFlow<ShapeSequence?> get() = _currentSequence

    private val _gameState = MutableStateFlow<GameState>(GameState.IDLE)
    val gameState: StateFlow<GameState> get() = _gameState

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> get() = _score

    private val _userProgress = MutableStateFlow(emptyList<Int>())
    val userProgress: StateFlow<List<Int>> get() = _userProgress

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private var sequenceStartTime = 0L

    init {
        loadGameData()
    }

    private fun loadGameData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Load levels from repository
                val gameLevels = shapeSequenceRepository.getGameLevels()
                _levels.value = gameLevels
                _currentLevel.value = gameLevels.firstOrNull()

                // Load user progress from Firebase
                loadUserProgress()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadUserProgress() {
        try {
            val progress = gameProgressRepository.getUserGameProgress("shape_sequence")
            progress?.completedLevels?.let { completedLevels ->
                _userProgress.value = completedLevels

                // Unlock levels based on progress
                _levels.value = _levels.value.map { level ->
                    if (completedLevels.contains(level.level - 1) || level.level == 1) {
                        level.copy(isLocked = false)
                    } else {
                        level
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setCurrentLevel(level: ShapeSequenceLevel) {
        _currentLevel.value = level
        resetGame()
    }

    fun startNewSequence() {
        val level = _currentLevel.value ?: return
        val newSequence = shapeSequenceRepository.generateSequence(level)

        _currentSequence.value = ShapeSequence(
            sequence = newSequence,
            userSequence = emptyList(),
            isShowing = true
        )

        _gameState.value = GameState.SHOWING_SEQUENCE
        sequenceStartTime = System.currentTimeMillis()

        // Automatically hide sequence after flash duration
        viewModelScope.launch {
            delay(level.flashDuration)
            _currentSequence.value = _currentSequence.value?.copy(isShowing = false)
            _gameState.value = GameState.USER_INPUT
        }
    }

    fun addUserShape(shape: String) {
        val current = _currentSequence.value ?: return
        if (_gameState.value != GameState.USER_INPUT) return

        val newUserSequence = current.userSequence + shape
        _currentSequence.value = current.copy(userSequence = newUserSequence)

        // Check if sequence is complete
        if (newUserSequence.size == current.sequence.size) {
            checkUserSequence()
        }
    }

    private fun checkUserSequence() {
        val currentSequence = _currentSequence.value ?: return
        val level = _currentLevel.value ?: return

        val timeTaken = System.currentTimeMillis() - sequenceStartTime
        val newScore = shapeSequenceRepository.calculateScore(
            currentSequence.sequence,
            currentSequence.userSequence,
            timeTaken,
            level
        )

        _score.value = newScore
        _gameState.value = GameState.RESULT

        // Save progress to Firebase if score meets requirement
        viewModelScope.launch {
            if (newScore >= level.requiredScore) {
                gameProgressRepository.updateCompletedLevel("shape_sequence", level.level, newScore)

                // Update local progress
                if (!_userProgress.value.contains(level.level)) {
                    _userProgress.value = _userProgress.value + level.level
                }

                // Unlock next level
                unlockNextLevel(level.level)
            } else {
                // Still save the score for tracking, even if not completed
                gameProgressRepository.updateCompletedLevel("shape_sequence", level.level, newScore)
            }
        }
    }

    private fun unlockNextLevel(completedLevel: Int) {
        _levels.value = _levels.value.map { level ->
            if (level.level == completedLevel + 1) {
                level.copy(isLocked = false)
            } else {
                level
            }
        }
    }

    fun resetGame() {
        _currentSequence.value = null
        _score.value = 0
        _gameState.value = GameState.IDLE
    }

    fun clearUserSequence() {
        val current = _currentSequence.value ?: return
        _currentSequence.value = current.copy(userSequence = emptyList())
    }

    fun retryLevel() {
        resetGame()
        startNewSequence()
    }

    sealed class GameState {
        object IDLE : GameState()
        object SHOWING_SEQUENCE : GameState()
        object USER_INPUT : GameState()
        object RESULT : GameState()
    }
}