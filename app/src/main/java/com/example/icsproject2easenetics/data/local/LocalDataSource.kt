package com.example.icsproject2easenetics.data.local

import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.data.models.DifficultyLevel
import com.example.icsproject2easenetics.data.models.LessonCategory

object LocalDataSource {
    val lessons = listOf(
        Lesson(
            lessonId = "lesson_smartphone_basics",
            moduleId = "module_1",
            title = "Getting Started with Your Smartphone",
            objective = "Learn the basics of using your smartphone",
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
            order = 1,
            hasQuiz = true
        ),
        Lesson(
            lessonId = "lesson_internet_safety",
            moduleId = "module_4",
            title = "Safe Internet Browsing",
            objective = "Stay safe while browsing the internet",
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
            order = 2,
            hasQuiz = true
        ),
        Lesson(
            lessonId = "lesson_1_1",
            moduleId = "module_1",
            title = "Meet Your Smartphone",
            objective = "To understand the physical parts of a smartphone and basic navigation.",
            description = "Learn the physical parts and basic navigation of your smartphone",
            content = """
                # Meet Your Smartphone
                
                Welcome to your new smartphone! Let's get familiar with the basic parts:
                
                ## 📱 Physical Parts:
                • **Power Button**: Usually on the right side - turns phone on/off
                • **Volume Buttons**: On the side - control sound volume
                • **Charging Port**: At the bottom - for charging cable
                • **Screen**: The main display you touch
                
                ## 🔋 First Steps:
                1. **Charging**: Plug in your phone overnight
                2. **Turning On**: Press and hold the power button
                3. **Unlocking**: Swipe up or enter your PIN
                
                ## 👆 Touchscreen Basics:
                • **Tap**: Lightly touch the screen to select
                • **Swipe**: Move finger across screen to scroll
                • **Pinch**: Use two fingers to zoom in/out on photos
                
                Take your time to explore - you'll get comfortable quickly!
            """.trimIndent(),
            duration = 15,
            difficulty = DifficultyLevel.BEGINNER,
            order = 1,
            hasQuiz = true
        ),
        Lesson(
            lessonId = "lesson_3_1",
            moduleId = "module_3",
            title = "M-Pesa Basics: Your Digital Wallet",
            objective = "To understand and securely access your M-Pesa account.",
            description = "Learn about M-Pesa and how to check your balance",
            content = """
                # M-Pesa Basics: Your Digital Wallet
                
                M-Pesa is Kenya's revolutionary mobile money service that turns your phone into a wallet!
                
                ## 💰 What is M-Pesa?
                • A safe way to send and receive money
                • Pay bills without going to offices
                • Buy airtime and data bundles
                • Withdraw cash from agents
                
                ## 🔒 Security First:
                • Your M-Pesa PIN is like your ATM PIN - keep it secret!
                • Never share your PIN with anyone
                • Safaricom will NEVER ask for your PIN
                
                ## 📱 Basic M-Pesa Functions:
                1. **Check Balance**: *334# then follow prompts
                2. **Send Money**: Select "Send Money" in M-Pesa menu
                3. **Buy Airtime**: Select "Buy Airtime" for yourself or others
                
                M-Pesa makes life easier and safer - no need to carry lots of cash!
            """.trimIndent(),
            duration = 25,
            difficulty = DifficultyLevel.BEGINNER,
            order = 1,
            hasQuiz = true
        )
    )

    // Sample quiz questions
    val quizQuestions = listOf(
        QuizQuestion(
            questionId = "q1_1_1",
            question = "What is the main function of a smartphone?",
            options = listOf(
                "Making calls and accessing the internet",
                "Only for taking photos",
                "Just for playing games",
                "Only for sending messages"
            ),
            correctAnswer = 0,
            explanation = "Smartphones are versatile devices that combine calling, internet access, photography, and many other functions."
        ),
        QuizQuestion(
            questionId = "q1_1_2",
            question = "How do you wake up your smartphone?",
            options = listOf(
                "Press the power button",
                "Shake the phone",
                "Say 'wake up'",
                "Plug it into charging"
            ),
            correctAnswer = 0,
            explanation = "The power button is used to wake up the phone from sleep mode."
        ),
        QuizQuestion(
            questionId = "q3_1_1",
            question = "What is M-Pesa?",
            options = listOf(
                "A mobile money service",
                "A type of smartphone",
                "A social media app",
                "A government website"
            ),
            correctAnswer = 0,
            explanation = "M-Pesa is Kenya's popular mobile money service that lets you send, receive, and store money on your phone."
        )
    )

    fun getLessonById(lessonId: String): Lesson? {
        return lessons.find { it.lessonId == lessonId }
    }

    // FIXED: Remove or fix the getLessonsByCategory function
    fun getLessonsByCategory(category: LessonCategory): List<Lesson> {
        return when (category) {
            LessonCategory.SMARTPHONE_BASICS -> lessons.filter { it.moduleId == "module_1" }
            LessonCategory.INTERNET_BROWSING -> lessons.filter { it.moduleId == "module_4" }
            LessonCategory.SOCIAL_MEDIA -> lessons.filter { it.moduleId == "module_2" }
            LessonCategory.ONLINE_SAFETY -> lessons.filter { it.moduleId == "module_4" }
            LessonCategory.COMMUNICATION -> lessons.filter { it.moduleId == "module_2" }
            else -> emptyList()
        }
    }

    // Get lessons by module
    fun getLessonsByModule(moduleId: String): List<Lesson> {
        return lessons.filter { it.moduleId == moduleId }.sortedBy { it.order }
    }

    // Get quiz questions for a lesson
    fun getQuizQuestions(lessonId: String): List<QuizQuestion> {

        return when (lessonId) {
            "lesson_1_1" -> quizQuestions.take(2)
            "lesson_3_1" -> quizQuestions.takeLast(1)
            "lesson_smartphone_basics" -> quizQuestions.take(2) // For backward compatibility
            else -> emptyList()
        }
    }

    // Get all lessons
    fun getAllLessons(): List<Lesson> {
        return lessons
    }
}