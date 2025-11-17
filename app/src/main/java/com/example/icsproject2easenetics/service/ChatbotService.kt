package com.example.icsproject2easenetics.service

import kotlinx.coroutines.delay

class ChatbotService {

    suspend fun getResponse(userInput: String, context: ChatContext = ChatContext()): ChatbotResponse {
        // Simulate API call delay
        delay(800)

        return when {
            // Greetings
            userInput.contains("hello", ignoreCase = true) ||
                    userInput.contains("hi", ignoreCase = true) ||
                    userInput.contains("hey", ignoreCase = true) -> {
                ChatbotResponse(
                    message = "Hello! I'm Mshauri, your digital learning assistant. I can help you with smartphones, internet safety, social media, and more. What would you like to learn about today?",
                    suggestedLessons = getRelevantLessons("basics"),
                    quickQuestions = listOf("What is a smartphone?", "How do I stay safe online?", "What is social media?")
                )
            }

            // Smartphone questions
            userInput.contains("smartphone", ignoreCase = true) ||
                    userInput.contains("phone", ignoreCase = true) && !userInput.contains("video call") -> {
                ChatbotResponse(
                    message = "A smartphone is like a small computer that fits in your pocket! You can use it to:\n\n• Make calls and send messages\n• Take photos and videos\n• Browse the internet\n• Use helpful apps like maps and weather\n• Connect with family on social media\n\nWould you like to learn more about any specific feature?",
                    suggestedLessons = getRelevantLessons("smartphone"),
                    quickQuestions = listOf("How do I make a call?", "How do I take photos?", "What are apps?")
                )
            }

            // Internet safety
            userInput.contains("safe", ignoreCase = true) ||
                    userInput.contains("security", ignoreCase = true) ||
                    userInput.contains("password", ignoreCase = true) -> {
                ChatbotResponse(
                    message = "Staying safe online is very important! Here are some key tips:\n\n🔒 Use strong, unique passwords\n🔒 Don't share personal information with strangers\n🔒 Look for 'https://' in website addresses\n🔒 Be careful with email attachments\n🔒 Use antivirus software\n\nI can walk you through each of these safety practices step by step.",
                    suggestedLessons = getRelevantLessons("safety"),
                    quickQuestions = listOf("What makes a good password?", "How to spot scams?", "Is public Wi-Fi safe?")
                )
            }

            // Social media
            userInput.contains("social media", ignoreCase = true) ||
                    userInput.contains("facebook", ignoreCase = true) ||
                    userInput.contains("instagram", ignoreCase = true) -> {
                ChatbotResponse(
                    message = "Social media helps you connect with family and friends! Popular platforms include:\n\n📘 Facebook - Share photos and updates\n📷 Instagram - Share pictures and stories\n💼 LinkedIn - Professional connections\n\nYou can see what your grandchildren are up to, share your own photos, and stay in touch with loved ones.",
                    suggestedLessons = getRelevantLessons("social"),
                    quickQuestions = listOf("How to create a Facebook account?", "Is social media safe?", "How to find family members?")
                )
            }

            // Video calls
            userInput.contains("video call", ignoreCase = true) ||
                    userInput.contains("facetime", ignoreCase = true) ||
                    userInput.contains("zoom", ignoreCase = true) -> {
                ChatbotResponse(
                    message = "Video calls let you see and talk to people face-to-face, even when they're far away! Popular apps:\n\n📹 Zoom - For group calls and meetings\n📱 FaceTime - For Apple devices\n💬 WhatsApp - Free calls and messages\n📞 Skype - Another popular option\n\nYou can have virtual family gatherings or see your grandchildren grow up!",
                    suggestedLessons = getRelevantLessons("video"),
                    quickQuestions = listOf("How to make a video call?", "What do I need for video calls?", "Is it free?")
                )
            }

            // Email
            userInput.contains("email", ignoreCase = true) ||
                    userInput.contains("gmail", ignoreCase = true) -> {
                ChatbotResponse(
                    message = "Email is like digital mail! You can send messages, photos, and documents to anyone with an email address. It's:\n\n✉️ Fast - Delivered in seconds\n🆓 Free - No stamps needed\n🌍 Global - Send anywhere in the world\n💾 Permanent - Keep important messages\n\nYou can use it to stay in touch with family, receive photos, and manage appointments.",
                    suggestedLessons = getRelevantLessons("email"),
                    quickQuestions = listOf("How to create an email account?", "How to send an email?", "What is spam?")
                )
            }

            // Default response
            else -> {
                ChatbotResponse(
                    message = "I'm here to help you learn digital skills! You can ask me about:\n\n📱 Smartphones and how to use them\n🔒 Internet safety and security\n📘 Social media like Facebook\n📹 Video calls with family\n✉️ Sending emails\n📸 Taking and sharing photos\n\nWhat would you like to learn about?",
                    suggestedLessons = getRelevantLessons("general"),
                    quickQuestions = listOf("What is a smartphone?", "How to stay safe online?", "What is Facebook?", "How to make video calls?")
                )
            }
        }
    }

    private fun getRelevantLessons(category: String): List<SuggestedLesson> {
        return when (category) {
            "smartphone" -> listOf(
                SuggestedLesson("lesson_smartphone_basics", "Getting Started with Your Smartphone", "Learn basic phone functions"),
                SuggestedLesson("lesson_video_calls", "Using Video Calls", "Make face-to-face calls")
            )
            "safety" -> listOf(
                SuggestedLesson("lesson_internet_safety", "Safe Internet Browsing", "Stay safe online"),
                SuggestedLesson("lesson_online_safety", "Online Safety Basics", "Protect yourself")
            )
            "social" -> listOf(
                SuggestedLesson("lesson_social_media", "Connecting with Family on Social Media", "Use Facebook and more")
            )
            "video" -> listOf(
                SuggestedLesson("lesson_video_calls", "Using Video Calls", "Make face-to-face calls")
            )
            "email" -> listOf(
                SuggestedLesson("lesson_smartphone_basics", "Getting Started with Your Smartphone", "Includes email setup")
            )
            else -> listOf(
                SuggestedLesson("lesson_smartphone_basics", "Getting Started with Your Smartphone", "Perfect for beginners"),
                SuggestedLesson("lesson_internet_safety", "Safe Internet Browsing", "Essential safety tips")
            )
        }
    }
}

data class ChatbotResponse(
    val message: String,
    val suggestedLessons: List<SuggestedLesson> = emptyList(),
    val quickQuestions: List<String> = emptyList()
)

data class SuggestedLesson(
    val lessonId: String,
    val title: String,
    val description: String
)

data class ChatContext(
    val lastTopic: String = "",
    val userSkillLevel: String = "beginner"
)