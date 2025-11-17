package com.example.icsproject2easenetics.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icsproject2easenetics.data.models.Lesson
import com.example.icsproject2easenetics.data.models.Module
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ModuleViewModel : ViewModel() {

    private val _modules = MutableStateFlow<List<Module>>(emptyList())
    val modules: StateFlow<List<Module>> = _modules.asStateFlow()

    private val _moduleLessons = MutableStateFlow<Map<String, List<Lesson>>>(emptyMap())
    val moduleLessons: StateFlow<Map<String, List<Lesson>>> = _moduleLessons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadAllModules() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // For now, use sample data - we'll implement Firebase later
                val sampleModules = getSampleModules()
                _modules.value = sampleModules

                // Load lessons for each module
                val lessonsMap = mutableMapOf<String, List<Lesson>>()
                sampleModules.forEach { module ->
                    val lessons = getSampleLessonsForModule(module.moduleId)
                    lessonsMap[module.moduleId] = lessons
                }
                _moduleLessons.value = lessonsMap
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load modules: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getLessonsForModule(moduleId: String): List<Lesson> {
        return _moduleLessons.value[moduleId] ?: emptyList()
    }

    private fun getSampleModules(): List<Module> {
        return listOf(
            Module(
                moduleId = "module_1",
                title = "Smartphone Fundamentals (The Basics)",
                description = "This module is about removing the initial fear and building a foundation of confidence.",
                order = 1,
                icon = "📱",
                totalLessons = 5
            ),
            Module(
                moduleId = "module_2",
                title = "Communication and Connection (Local Essentials)",
                description = "This module focuses on the most popular ways Kenyans connect with family and friends.",
                order = 2,
                icon = "💬",
                totalLessons = 3
            ),
            Module(
                moduleId = "module_3",
                title = "Mobile Money (M-Pesa) – The Kenyan Wallet",
                description = "This module is critical for financial independence and is a cornerstone of Kenyan digital life.",
                order = 3,
                icon = "💰",
                totalLessons = 4
            ),
            Module(
                moduleId = "module_4",
                title = "Online Safety and Security (Empowerment)",
                description = "This module is vital for building trust and protecting seniors from common local scams.",
                order = 4,
                icon = "🔒",
                totalLessons = 3
            ),
            Module(
                moduleId = "module_5",
                title = "Government & Essential Services (eCitizen & KRA)",
                description = "This module directly addresses key government portals, which is essential for civic empowerment.",
                order = 5,
                icon = "🏛️",
                totalLessons = 5
            )
        )
    }

    private fun getSampleLessonsForModule(moduleId: String): List<Lesson> {
        return when (moduleId) {
            "module_1" -> listOf(
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
                    order = 1,
                    hasQuiz = true
                ),
                Lesson(
                    lessonId = "lesson_1_2",
                    moduleId = "module_1",
                    title = "Making and Receiving Phone Calls",
                    objective = "To use the phone for its most basic function: calling.",
                    description = "Learn how to make and receive calls",
                    content = """
                        # Making and Receiving Phone Calls
                        
                        Your smartphone's most important function is making calls. Let's learn how:
                        
                        ## 📞 Making a Call:
                        1. **Open Phone App**: Tap the phone icon
                        2. **Dial Number**: Use the keypad to enter the number
                        3. **Make Call**: Tap the green call button
                        4. **End Call**: Tap the red end button
                        
                        ## 📲 Receiving a Call:
                        • **Answer**: Swipe the green button to answer
                        • **Decline**: Swipe the red button to decline
                        • **Ignore**: Let it ring if you're busy
                        
                        ## 👥 Using Contacts:
                        • Save frequently called numbers as contacts
                        • Tap a contact name to call them directly
                        • Add photos to recognize callers
                        
                        Practice calling a family member to get comfortable!
                    """.trimIndent(),
                    duration = 20,
                    order = 2,
                    hasQuiz = true
                )
            )
            "module_2" -> listOf(
                Lesson(
                    lessonId = "lesson_2_1",
                    moduleId = "module_2",
                    title = "Introduction to WhatsApp",
                    objective = "To set up and understand the primary communication app in Kenya.",
                    description = "Learn about WhatsApp setup and basic features",
                    content = """
                        # Introduction to WhatsApp
                        
                        WhatsApp is Kenya's most popular messaging app - let's get started!
                        
                        ## 📱 What is WhatsApp?
                        • Free messaging app (uses Wi-Fi or data)
                        • Send texts, photos, videos, and voice messages
                        • Make free voice and video calls
                        • Popular with family and friends in Kenya
                        
                        ## 🔧 Setting Up WhatsApp:
                        1. **Download**: Get WhatsApp from Play Store
                        2. **Verify**: Enter your phone number for verification
                        3. **Profile**: Add your name and photo
                        4. **Contacts**: WhatsApp finds your contacts automatically
                        
                        ## 💬 Basic Features:
                        • **Chats**: One-on-one conversations
                        • **Groups**: Family or community group chats
                        • **Status**: Share updates that disappear in 24 hours
                        
                        WhatsApp keeps you connected with loved ones for free!
                    """.trimIndent(),
                    duration = 25,
                    order = 1,
                    hasQuiz = true
                )
            )
            "module_3" -> listOf(
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
                    order = 1,
                    hasQuiz = true
                )
            )
            "module_4" -> listOf(
                Lesson(
                    lessonId = "lesson_4_1",
                    moduleId = "module_4",
                    title = "Spotting Common M-Pesa Scams",
                    objective = "To identify and ignore the most common scams in Kenya.",
                    description = "Learn how to recognize and avoid M-Pesa scams",
                    content = """
                        # Spotting Common M-Pesa Scams
                        
                        Protect yourself from fraudsters with these important safety tips:
                        
                        ## 🚨 Common Scams to Avoid:
                        
                        ### 1. The "Wrong Number" Scam:
                        • You receive fake M-Pesa message saying money was sent to you
                        • Scammer calls claiming they sent money by mistake
                        • They ask you to "send it back"
                        • **REALITY**: If it was real, they can reverse it themselves
                        
                        ### 2. The "Fuliza" Scam:
                        • Fake message: "Your Fuliza limit has been increased"
                        • Contains suspicious link to click
                        • **REALITY**: Never click links in unexpected messages
                        
                        ### 3. Impersonation Calls:
                        • Caller pretends to be from Safaricom, bank, or KRA
                        • Asks for your PIN or personal details
                        • **REALITY**: Legitimate companies NEVER ask for your PIN
                        
                        ## 🛡️ Safety Rules:
                        • Never share your M-Pesa PIN with anyone
                        • Don't click suspicious links
                        • Verify unexpected messages by calling official numbers
                        • When in doubt, don't send money!
                    """.trimIndent(),
                    duration = 30,
                    order = 1,
                    hasQuiz = true
                )
            )
            "module_5" -> listOf(
                Lesson(
                    lessonId = "lesson_5_1",
                    moduleId = "module_5",
                    title = "What is eCitizen (Gava Mkononi)?",
                    objective = "To understand what eCitizen is and how to create an account.",
                    description = "Learn about the eCitizen government portal",
                    content = """
                        # What is eCitizen (Gava Mkononi)?
                        
                        eCitizen is the official Kenyan government portal for all services!
                        
                        ## 🏛️ What is eCitizen?
                        • One-stop platform for all government services
                        • Access services from home using your phone
                        • Pay for services securely online
                        • Track application progress
                        
                        ## 📝 Services Available:
                        • Apply for passports and IDs
                        • Pay for business permits
                        • Access NSSF and NHIF services
                        • Apply for driving licenses
                        • And many more services!
                        
                        ## 🔐 Creating an Account:
                        1. Visit www.ecitizen.go.ke
                        2. Click "Register" and enter your ID number
                        3. Verify your mobile number
                        4. Set a strong password
                        5. Start accessing services!
                        
                        eCitizen saves you time and travel - government services at your fingertips!
                    """.trimIndent(),
                    duration = 20,
                    order = 1,
                    hasQuiz = true
                )
            )
            else -> emptyList()
        }
    }
}

// Temporary empty repository (if needed elsewhere)
class LessonRepository {
    suspend fun getAllModules(): List<Module> = emptyList()
    suspend fun getLessonsByModule(moduleId: String): List<Lesson> = emptyList()
    suspend fun getLessonById(lessonId: String): Lesson? = null
    suspend fun getQuizQuestions(lessonId: String): List<com.example.icsproject2easenetics.data.models.QuizQuestion> = emptyList()
}