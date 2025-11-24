package com.example.icsproject2easenetics.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.icsproject2easenetics.data.models.Game
import com.example.icsproject2easenetics.service.ChatPreferences
import com.example.icsproject2easenetics.ui.screens.*
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ChatbotViewModel
import com.example.icsproject2easenetics.ui.viewmodels.ChatbotViewModelFactory
import com.example.icsproject2easenetics.ui.viewmodels.QuizViewModel
import com.example.icsproject2easenetics.ui.screens.ShapeSequenceGameScreen
import com.example.icsproject2easenetics.ui.viewmodels.ShapeSequenceViewModel

@Composable
fun AppNavigation(context: Context) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "onboarding") {

        // Onboarding
        composable("onboarding") {
            OnboardingScreen(onGetStarted = {
                if (authViewModel.currentUser.value != null) {
                    navController.navigate("dashboard")
                } else {
                    navController.navigate("login")
                }
            })
        }

        // Login
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") },
                onBack = { navController.popBackStack() }
            )
        }

        // Register
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate("dashboard") { popUpTo("register") { inclusive = true } } },
                onNavigateToLogin = { navController.navigate("login") },
                onBack = { navController.popBackStack() }
            )
        }

        // Dashboard
        composable("dashboard") {
            DashboardScreen(
                onLessonClick = { lessonId -> navController.navigate("lesson/$lessonId") },
                onChatbotClick = { navController.navigate("chatbot") },
                onProfileClick = { navController.navigate("profile") },
                onProgressClick = { navController.navigate("progress") },
                onModulesClick = { navController.navigate("modules") },
                onWisdomSharingClick = { navController.navigate("wisdom_sharing") },
                onGamesClick = { navController.navigate("games") }
            )
        }

        composable("chatbot") {
            // Inject ChatPreferences into ViewModel
            val chatPrefs = ChatPreferences(context)
            val factory = ChatbotViewModelFactory(chatPrefs)
            val chatbotViewModel: ChatbotViewModel = viewModel(factory = factory)

            ChatbotScreen(
                viewModel = chatbotViewModel,
                onBack = { navController.popBackStack() },
                onLessonClick = { lessonId: String ->
                    navController.navigate("lesson/$lessonId")
                }
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onAccessibilityClick = { navController.navigate("accessibility") },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                }
            )
        }

        // Accessibility
        composable("accessibility") {
            AccessibilityScreen(onBack = { navController.popBackStack() })
        }

        // Progress
        composable("progress") {
            ProgressScreen(onBack = { navController.popBackStack() })
        }

        // Modules
        composable("modules") {
            ModulesScreen(
                onBack = { navController.popBackStack() },
                onModuleClick = { moduleId -> navController.navigate("module_lessons/$moduleId") }
            )
        }

        // Module Lessons
        composable(
            "module_lessons/{moduleId}",
            arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val moduleId = backStackEntry.arguments?.getString("moduleId") ?: ""
            ModuleLessonsScreen(
                moduleId = moduleId,
                onBack = { navController.popBackStack() },
                onLessonClick = { lessonId -> navController.navigate("lesson/$lessonId") }
            )
        }

        // Lesson Screen
        composable(
            "lesson/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() },
                onStartQuiz = { quizLessonId -> navController.navigate("quiz/$quizLessonId") },
                onMarkComplete = { navController.popBackStack() }
            )
        }

// In the games composable, add explicit type
        composable("games") {
            GamesScreen(
                onBack = { navController.popBackStack() },
                onGameSelected = { game: Game ->
                    when (game.id) {
                        "shape_sequence" -> navController.navigate("shape_sequence_game")
                        // Add other games when implemented
                        else -> { /* Handle other games */ }
                    }
                }
            )
        }

        composable("shape_sequence_game") {
            ShapeSequenceGameScreen(
                onBack = { navController.popBackStack() }
            )
        }
        // Quiz Screen
        composable(
            "quiz/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            val quizViewModel: QuizViewModel = viewModel()

            LaunchedEffect(lessonId) { quizViewModel.loadQuizQuestions(lessonId) }

            val questions by quizViewModel.questions.collectAsState()
            val isLoading by quizViewModel.isLoading.collectAsState()
            val errorMessage by quizViewModel.errorMessage.collectAsState()

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text("Loading quiz questions...")
                }
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = errorMessage ?: "Failed to load quiz", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { quizViewModel.loadQuizQuestions(lessonId) }) {
                        Text("Retry")
                    }
                }
            } else {
                QuizScreen(
                    lessonId = lessonId,
                    questions = questions,
                    onQuizComplete = { score, total ->
                        // Keep score and total available for logging, saving, or analytics if needed
                        println("Quiz finished. Score: $score / $total")

                        // Then navigate back
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )

            }
        }
    }
}
