package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            // Add user message
            val newMessages = _chatMessages.value.toMutableList()
            newMessages.add(ChatMessage(content = userMessage, isUser = true))
            _chatMessages.value = newMessages

            // Simulate AI response
            _isLoading.value = true
            delay(1000) // Simulate API call

            val aiResponse = generateAIResponse(userMessage)
            newMessages.add(ChatMessage(content = aiResponse, isUser = false))
            _chatMessages.value = newMessages
            _isLoading.value = false
        }
    }

    private fun generateAIResponse(userMessage: String): String {
        return when {
            userMessage.contains("smartphone", ignoreCase = true) -> {
                "A smartphone is a mobile device that combines cellular and mobile computing " +
                        "capabilities. Think of it as a small computer that fits in your pocket! " +
                        "You can make calls, send messages, take photos, browse the internet, and use helpful apps."
            }
            userMessage.contains("email", ignoreCase = true) -> {
                "Email is like digital mail. You can send messages, photos, and documents to anyone " +
                        "with an email address. It's fast, free, and you can access it from anywhere. " +
                        "Would you like me to explain how to create an email account?"
            }
            userMessage.contains("social media", ignoreCase = true) -> {
                "Social media platforms like Facebook help you connect with family and friends. " +
                        "You can share photos, send messages, and see what your loved ones are up to. " +
                        "It's a great way to stay connected!"
            }
            else -> {
                "I'm here to help you learn digital skills! You can ask me about smartphones, " +
                        "internet safety, social media, video calls, or anything else you'd like to learn. " +
                        "What specific topic would you like help with?"
            }
        }
    }
}

data class ChatMessage(
    val content: String,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)