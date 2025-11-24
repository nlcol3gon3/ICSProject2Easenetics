package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.service.ChatPreferences
import com.example.icsproject2easenetics.service.GoogleAIChatService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel(
    private val service: GoogleAIChatService,
    private val preferences: ChatPreferences
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<String>>(emptyList())
    val chatHistory: StateFlow<List<String>> get() = _chatHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun sendMessage(message: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Add user message
            _chatHistory.value = _chatHistory.value + "You: $message"

            try {
                // Get AI response
                val response = service.getResponse(message)
                _chatHistory.value = _chatHistory.value + "AI: ${response.message}"

                // Save chat history
                preferences.saveChatHistory(_chatHistory.value)
            } catch (e: Exception) {
                e.printStackTrace()
                _chatHistory.value = _chatHistory.value + "AI: Error - ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Add this function to clear chat history
    fun clearChatHistory() {
        viewModelScope.launch {
            _chatHistory.value = emptyList()
            preferences.saveChatHistory(emptyList())
        }
    }

    fun loadChatHistory() {
        viewModelScope.launch {
            _chatHistory.value = preferences.loadChatHistory()
        }
    }
}