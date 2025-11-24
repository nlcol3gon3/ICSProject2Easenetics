package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.icsproject2easenetics.service.ChatPreferences
import com.example.icsproject2easenetics.service.GoogleAIChatService

class ChatbotViewModelFactory(private val preferences: ChatPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatbotViewModel(
                // Use your actual API key
                service = GoogleAIChatService("AIzaSyBemoFYAikj3eCVTDCBHrNjxX6o0ZI9CfA"),
                preferences = preferences
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}