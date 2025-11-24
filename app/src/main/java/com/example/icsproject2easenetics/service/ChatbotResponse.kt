package com.example.icsproject2easenetics.service

data class SuggestedLesson(
    val id: String,
    val title: String
)

data class ChatbotResponse(
    val message: String,
    val suggestedLessons: List<SuggestedLesson> = emptyList(),
    val quickQuestions: List<String> = emptyList()
)
