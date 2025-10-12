package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.service.ChatbotService
import com.example.icsproject2easenetics.service.SuggestedLesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val chatbotService = ChatbotService()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _suggestedLessons = MutableStateFlow<List<SuggestedLesson>>(emptyList())
    val suggestedLessons: StateFlow<List<SuggestedLesson>> = _suggestedLessons.asStateFlow()

    private val _quickQuestions = MutableStateFlow<List<String>>(emptyList())
    val quickQuestions: StateFlow<List<String>> = _quickQuestions.asStateFlow()

    init {
        // Send welcome message when chatbot starts
        sendWelcomeMessage()
    }

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            // Add user message
            val newMessages = _chatMessages.value.toMutableList()
            newMessages.add(ChatMessage(content = userMessage, isUser = true))
            _chatMessages.value = newMessages

            // Get AI response
            _isLoading.value = true
            val response = chatbotService.getResponse(userMessage)

            // Add AI response
            newMessages.add(ChatMessage(content = response.message, isUser = false))
            _chatMessages.value = newMessages

            // Update suggestions
            _suggestedLessons.value = response.suggestedLessons
            _quickQuestions.value = response.quickQuestions

            _isLoading.value = false
        }
    }

    private fun sendWelcomeMessage() {
        viewModelScope.launch {
            val welcomeResponse = chatbotService.getResponse("hello")
            _chatMessages.value = listOf(
                ChatMessage(content = welcomeResponse.message, isUser = false)
            )
            _suggestedLessons.value = welcomeResponse.suggestedLessons
            _quickQuestions.value = welcomeResponse.quickQuestions
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        _suggestedLessons.value = emptyList()
        _quickQuestions.value = emptyList()
        sendWelcomeMessage()
    }
}

data class ChatMessage(
    val content: String,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)