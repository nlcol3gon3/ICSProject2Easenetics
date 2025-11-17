package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.icsproject2easenetics.ui.screens.AccessibilityScreen
import com.example.icsproject2easenetics.ui.screens.AuthenticatorSetupScreen
import com.example.icsproject2easenetics.ui.screens.ChatbotScreen
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.LessonScreen
import com.example.icsproject2easenetics.ui.screens.LoginScreen
import com.example.icsproject2easenetics.ui.screens.MfaVerificationScreen
import com.example.icsproject2easenetics.ui.screens.ModuleLessonsScreen
import com.example.icsproject2easenetics.ui.screens.ModulesScreen
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
                },
                onModulesClick = {
                    navController.navigate("modules")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = {
                    navController.popBackStack()
                },
                onAccessibilityClick = {
                    navController.navigate("accessibility")
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

        // Accessibility Screen
        composable("accessibility") {
            AccessibilityScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Progress Screen
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

        // Modules Screen - NEW
        composable("modules") {
            ModulesScreen(
                onBack = {
                    navController.popBackStack()
                },
                onModuleClick = { moduleId ->
                    navController.navigate("module_lessons/$moduleId")
                }
            )
        }

        // Module Lessons Screen - NEW
        composable(
            "module_lessons/{moduleId}",
            arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            ModuleLessonsScreen(
                moduleId = moduleId,
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
                onStartQuiz = { quizLessonId -> // FIXED: Removed the second parameter
                    navController.navigate("quiz/$quizLessonId")
                },
                onMarkComplete = {
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

            // Sample questions - In real implementation, these will be fetched from Firebase
            val sampleQuestions = when (lessonId) {
                "lesson_1_1" -> listOf(
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
                    )
                )
                "lesson_3_1" -> listOf(
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
                else -> emptyList()
            }

            QuizScreen(
                lessonId = lessonId,
                questions = sampleQuestions,
                onQuizComplete = { score, total ->
                    // In real implementation, save progress to Firebase
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}