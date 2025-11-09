package com.example.icsproject2easenetics.data.local

import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.data.models.DifficultyLevel
import com.example.icsproject2easenetics.data.models.LessonCategory

object LocalDataSource {
    val lessons = listOf(
        Lesson(
            lessonId = "lesson_smartphone_basics",
            title = "Getting Started with Your Smartphone",
            description = "Learn the basics of using your smartphone",
            content = """
                # Welcome to Your Smartphone!
                
                Your smartphone is like a small computer that fits in your pocket. Let's learn the basics:
                
                ## 📱 Basic Functions:
                • **Making Calls**: Tap the phone icon, enter number, tap green call button
                • **Sending Messages**: Tap messages icon, select contact, type your message
                • **Taking Photos**: Open camera app, point at subject, tap shutter button
                
                ## 🔋 Battery Tips:
                • Charge your phone overnight
                • Dim screen brightness to save battery
                • Close apps you're not using
                
                ## ⚙️ Basic Settings:
                • **Volume**: Use side buttons to adjust
                • **Wi-Fi**: Swipe down from top, tap Wi-Fi icon
                • **Brightness**: Swipe down from top, adjust slider
                
                Practice these steps with your phone in hand!
            """.trimIndent(),
            duration = 15,
            difficulty = DifficultyLevel.BEGINNER,
            category = LessonCategory.SMARTPHONE_BASICS,
            order = 1,
            hasQuiz = true,
            quizQuestions = listOf(
                QuizQuestion(
                    questionId = "q1",
                    question = "What is the main purpose of a smartphone?",
                    options = listOf(
                        "Communication and internet access",
                        "Only for emergency calls",
                        "Just for taking photos",
                        "Only for games"
                    ),
                    correctAnswer = 0,
                    explanation = "Smartphones are versatile devices for communication, internet, photos, and many useful functions."
                ),
                QuizQuestion(
                    questionId = "q2",
                    question = "How do you make a phone call?",
                    options = listOf(
                        "Tap phone icon, enter number, tap call button",
                        "Shake the phone and say 'call'",
                        "Press all buttons at once",
                        "Put phone to ear and wait"
                    ),
                    correctAnswer = 0,
                    explanation = "Use the phone app to dial numbers and make calls safely."
                )
            )
        ),
        // Add more lessons here...
        Lesson(
            lessonId = "lesson_internet_safety",
            title = "Safe Internet Browsing",
            description = "Stay safe while browsing the internet",
            content = """
                # Staying Safe Online
                
                The internet is wonderful but requires caution. Here's how to stay safe:
                
                ## 🔒 Password Security:
                • Use strong, unique passwords
                • Combine letters, numbers, and symbols
                • Never share passwords with anyone
                
                ## 📧 Email Safety:
                • Don't open emails from strangers
                • Never click suspicious links
                • Look for spelling mistakes in emails
                
                ## 🌐 Website Safety:
                • Look for 🔒 or 'https://' in address bar
                • Avoid entering personal info on unfamiliar sites
                • Use reputable websites for shopping
                
                Remember: If something seems too good to be true, it probably is!
            """.trimIndent(),
            duration = 20,
            difficulty = DifficultyLevel.BEGINNER,
            category = LessonCategory.ONLINE_SAFETY,
            order = 2,
            hasQuiz = true,
            quizQuestions = listOf(
                QuizQuestion(
                    questionId = "q1",
                    question = "What makes a strong password?",
                    options = listOf(
                        "Mix of letters, numbers, and symbols",
                        "Your pet's name",
                        "123456",
                        "password"
                    ),
                    correctAnswer = 0,
                    explanation = "Strong passwords combine different character types for better security."
                )
            )
        )
    )

    fun getLessonById(lessonId: String): Lesson? {
        return lessons.find { it.lessonId == lessonId }
    }

    fun getLessonsByCategory(category: LessonCategory): List<Lesson> {
        return lessons.filter { it.category == category }
    }
}