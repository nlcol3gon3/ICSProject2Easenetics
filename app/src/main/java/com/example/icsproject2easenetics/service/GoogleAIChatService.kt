package com.example.icsproject2easenetics.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GoogleAIChatService(private val apiKey: String) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Try multiple endpoints for better reliability
    private val endpoints = listOf(
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey",
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=$apiKey",
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash-001:generateContent?key=$apiKey"
    )

    suspend fun getResponse(userInput: String): ChatbotResponse = withContext(Dispatchers.IO) {
        var lastError = ""
        var retryCount = 0
        val maxRetries = 2

        for (endpoint in endpoints) {
            while (retryCount <= maxRetries) {
                try {
                    println("🚀 Attempt ${retryCount + 1}: Trying endpoint: ${endpoint.split("?")[0]}")

                    val jsonBody = """
                    {
                        "contents": [
                            {
                                "parts": [
                                    {
                                        "text": "Please format your response using markdown-like syntax for better readability:\n- Use # for main headings\n- Use ## for subheadings  \n- Use **bold** for important terms\n- Use clear paragraph breaks\n- Structure your response with proper formatting\n\nUser question: $userInput"
                                    }
                                ]
                            }
                        ],
                        "generationConfig": {
                            "temperature": 0.7,
                            "topP": 0.95,
                            "topK": 40,
                            "maxOutputTokens": 1024
                        }
                    }
                    """.trimIndent()

                    val request = Request.Builder()
                        .url(endpoint)
                        .post(jsonBody.toRequestBody("application/json".toMediaType()))
                        .addHeader("Content-Type", "application/json")
                        .build()

                    val response = client.newCall(request).execute()
                    val body = response.body?.string() ?: ""

                    println("📡 Response code: ${response.code}")

                    if (response.isSuccessful) {
                        println("✅ SUCCESS! Response received")

                        val json = JSONObject(body)
                        val candidates = json.getJSONArray("candidates")
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        val firstPart = parts.getJSONObject(0)
                        val messageText = firstPart.getString("text")

                        println("💡 AI Response: $messageText")

                        val suggestedLessons = createSuggestedLessons(messageText)
                        val quickQuestions = listOf(
                            "What is artificial intelligence?",
                            "Explain machine learning basics",
                            "How does neural networks work?",
                            "What is deep learning?",
                            "How can I start learning programming?"
                        )

                        return@withContext ChatbotResponse(
                            message = messageText,
                            suggestedLessons = suggestedLessons,
                            quickQuestions = quickQuestions
                        )
                    } else {
                        // Handle specific HTTP errors
                        val errorMessage = when (response.code) {
                            503 -> "Mshauri is currently busy. Please try again in a moment."
                            429 -> "Too many requests. Please wait a moment before trying again."
                            401 -> "API authentication failed. Please check your API key."
                            400 -> "Invalid request. Please try a different question."
                            else -> "Temporary service issue. Please try again."
                        }

                        lastError = "❌ $errorMessage (Code: ${response.code})"
                        println(lastError)

                        if (response.code == 503 || response.code == 429) {
                            retryCount++
                            if (retryCount <= maxRetries) {
                                println("🔄 Retrying in 2 seconds...")
                                kotlinx.coroutines.delay(2000)
                                continue
                            }
                        }
                        break
                    }
                } catch (e: Exception) {
                    lastError = "❌ Network error: ${e.message}"
                    println(lastError)
                    retryCount++
                    if (retryCount <= maxRetries) {
                        println("🔄 Retrying in 2 seconds...")
                        kotlinx.coroutines.delay(2000)
                    }
                }
                break
            }
            retryCount = 0
        }

        return@withContext ChatbotResponse(
            message = "# Service Temporarily Unavailable\n\nI'm experiencing high demand right now. Please try again in a moment.\n\n## What you can do:\n- **Try asking your question again** in a few minutes\n- **Check your internet connection**\n- **Try a simpler question** if possible\n\nI'll be back to help you learn as soon as possible!",
            suggestedLessons = emptyList(),
            quickQuestions = listOf(
                "What is artificial intelligence?",
                "Explain machine learning basics",
                "How can I start learning programming?"
            )
        )
    }

    private fun createSuggestedLessons(responseText: String): List<SuggestedLesson> {
        val lessons = mutableListOf<SuggestedLesson>()
        val text = responseText.lowercase()

        if (text.contains("ai") || text.contains("artificial intelligence")) {
            lessons.add(SuggestedLesson("lesson_ai", "Introduction to AI"))
        }
        if (text.contains("machine learning") || text.contains("ml")) {
            lessons.add(SuggestedLesson("lesson_ml", "Machine Learning Basics"))
        }
        if (text.contains("neural") || text.contains("deep learning")) {
            lessons.add(SuggestedLesson("lesson_dl", "Deep Learning Fundamentals"))
        }
        if (text.contains("programming") || text.contains("code") || text.contains("python") || text.contains("java") || text.contains("javascript")) {
            lessons.add(SuggestedLesson("lesson_programming", "Programming Basics"))
        }
        if (text.contains("data") || text.contains("analysis") || text.contains("analytics")) {
            lessons.add(SuggestedLesson("lesson_data", "Data Analysis"))
        }
        if (text.contains("web") || text.contains("development") || text.contains("html") || text.contains("css")) {
            lessons.add(SuggestedLesson("lesson_web", "Web Development"))
        }
        if (text.contains("mobile") || text.contains("android") || text.contains("ios")) {
            lessons.add(SuggestedLesson("lesson_mobile", "Mobile Development"))
        }

        // Always include some basic lessons
        if (lessons.isEmpty()) {
            lessons.addAll(listOf(
                SuggestedLesson("lesson_ai", "Introduction to AI"),
                SuggestedLesson("lesson_ml", "Machine Learning Basics"),
                SuggestedLesson("lesson_programming", "Programming Fundamentals")
            ))
        }

        return lessons.take(3) // Limit to 3 suggestions
    }
}