package com.example.icsproject2easenetics.service

import android.content.Context
import kotlinx.coroutines.delay

// Data models
data class ChatMessage(val sender: String, val text: String)
data class Lesson(val title: String)
data class QuickQuestion(val question: String)

class ChatbotService {

    private val messages = mutableListOf<ChatMessage>()

    fun loadChatHistory(context: Context): List<ChatMessage> {
        // Simulate previously sent messages
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Bot", "Hello! How can I help you today?"))
        }
        return messages
    }

    fun sendMessage(context: Context, text: String) {
        // Simulate sending message
        messages.add(ChatMessage("User", text))
        messages.add(ChatMessage("Bot", "You said: $text"))
    }

    fun sendQuickQuestion(context: Context, question: QuickQuestion) {
        // Simulate quick question response
        messages.add(ChatMessage("User", question.question))
        messages.add(ChatMessage("Bot", "Answering quick question: ${question.question}"))
    }

    fun getSuggestedLessons(): List<Lesson> {
        return listOf(Lesson("Lesson 1"), Lesson("Lesson 2"))
    }

    fun getQuickQuestions(): List<QuickQuestion> {
        return listOf(
            QuickQuestion("What is Kotlin?"),
            QuickQuestion("Explain Compose basics")
        )
    }
}
