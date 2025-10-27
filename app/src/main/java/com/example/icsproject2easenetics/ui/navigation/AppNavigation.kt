package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.icsproject2easenetics.ui.screens.AuthenticatorSetupScreen
import com.example.icsproject2easenetics.ui.screens.ChatbotScreen
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.LessonScreen
import com.example.icsproject2easenetics.ui.screens.LoginScreen
import com.example.icsproject2easenetics.ui.screens.MfaVerificationScreen
import com.example.icsproject2easenetics.ui.screens.OnboardingScreen
import com.example.icsproject2easenetics.ui.screens.ProgressScreen
import com.example.icsproject2easenetics.ui.screens.ProfileScreen
import com.example.icsproject2easenetics.ui.screens.QuizScreen
import com.example.icsproject2easenetics.ui.screens.RegisterScreen
import com.example.icsproject2easenetics.data.models.QuizQuestion
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    if (authViewModel.currentUser.value != null) {
                        navController.navigate("dashboard")
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    val currentUser = authViewModel.currentUser.value
                    currentUser?.let { user ->
                        if (authViewModel.mfaVerificationState.value is com.example.icsproject2easenetics.ui.viewmodels.MfaVerificationState.Required) {
                            navController.navigate("mfa_verification")
                        } else {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    } ?: run {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onLessonClick = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                },
                onChatbotClick = {
                    navController.navigate("chatbot")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onProgressClick = {
                    navController.navigate("progress")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }

        // Progress Screen - NEW
        composable("progress") {
            ProgressScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // MFA Verification Screen
        composable("mfa_verification") {
            MfaVerificationScreen(
                onVerificationSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("mfa_verification") { inclusive = true }
                    }
                },
                onBack = {
                    authViewModel.logout()
                    authViewModel.clearMfaState()
                    navController.navigate("login") {
                        popUpTo("mfa_verification") { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        // Authenticator App Setup Screen
        composable("authenticator_setup") {
            AuthenticatorSetupScreen(
                onSetupComplete = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }

        // Chatbot Screen
        composable("chatbot") {
            ChatbotScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLessonClick = { lessonId ->
                    navController.navigate("lesson/$lessonId")
                }
            )
        }

        // Lesson Screen with parameter
        composable(
            "lesson/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonScreen(
                lessonId = lessonId,
                onBack = {
                    navController.popBackStack()
                },
                onStartQuiz = { quizLessonId, questions ->
                    navController.navigate("quiz/$quizLessonId")
                },
                onMarkComplete = {
                    // Mark lesson complete and navigate back
                    navController.popBackStack()
                }
            )
        }

        // Quiz Screen with parameter
        composable(
            "quiz/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""

            // Sample questions - in real app, fetch from ViewModel based on lessonId
            val sampleQuestions = when (lessonId) {
                "lesson_smartphone_basics" -> listOf(
                    QuizQuestion(
                        questionId = "1",
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
                        questionId = "2",
                        question = "How can you stay safe online?",
                        options = listOf(
                            "Use strong passwords and don't share personal information",
                            "Share your password with family members",
                            "Click on all email links",
                            "Use the same password for everything"
                        ),
                        correctAnswer = 0,
                        explanation = "Strong, unique passwords and careful sharing of personal information are key to online safety."
                    ),
                    QuizQuestion(
                        questionId = "3",
                        question = "What does the Wi-Fi symbol look like?",
                        options = listOf(
                            "A curved signal icon",
                            "A battery icon",
                            "A gear icon",
                            "A heart icon"
                        ),
                        correctAnswer = 0,
                        explanation = "The Wi-Fi symbol typically looks like curved signal bars getting larger."
                    )
                )
                "lesson_internet_safety" -> listOf(
                    QuizQuestion(
                        questionId = "1",
                        question = "What should you do with suspicious emails?",
                        options = listOf(
                            "Delete them without opening",
                            "Open all attachments",
                            "Reply with personal information",
                            "Forward to friends"
                        ),
                        correctAnswer = 0,
                        explanation = "Suspicious emails may contain viruses or scams. It's safest to delete them."
                    ),
                    QuizQuestion(
                        questionId = "2",
                        question = "What indicates a secure website?",
                        options = listOf(
                            "https:// and a lock icon",
                            "Bright flashing colors",
                            "Lots of pop-up ads",
                            "No contact information"
                        ),
                        correctAnswer = 0,
                        explanation = "Secure websites use https:// and display a lock icon in the address bar."
                    )
                )
                "lesson_social_media" -> listOf(
                    QuizQuestion(
                        questionId = "1",
                        question = "What should you share on social media?",
                        options = listOf(
                            "General updates and safe photos",
                            "Your home address and phone number",
                            "Bank account details",
                            "Social Security number"
                        ),
                        correctAnswer = 0,
                        explanation = "Only share information you're comfortable with everyone seeing. Avoid personal details."
                    )
                )
                "lesson_video_calls" -> listOf(
                    QuizQuestion(
                        questionId = "1",
                        question = "Which app is NOT typically used for video calls?",
                        options = listOf(
                            "Calculator app",
                            "Zoom",
                            "FaceTime",
                            "WhatsApp"
                        ),
                        correctAnswer = 0,
                        explanation = "Calculator apps are for math, not video calls. Zoom, FaceTime, and WhatsApp support video calling."
                    )
                )
                else -> emptyList()
            }

            QuizScreen(
                lessonId = lessonId,
                questions = sampleQuestions,
                onQuizComplete = { score, total ->
                    // Update progress and navigate back to lesson
                    // In real app, save score to user progress
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}