package com.example.icsproject2easenetics.ui.navigation

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.example.icsproject2easenetics.utils.extractUserName
import com.example.icsproject2easenetics.ui.screens.AccessibilityScreen
import com.example.icsproject2easenetics.ui.screens.AuthenticatorSetupScreen
import com.example.icsproject2easenetics.ui.screens.ChatbotScreen
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.DebugScreen
import com.example.icsproject2easenetics.ui.screens.LessonScreen
import com.example.icsproject2easenetics.ui.screens.LoginScreen
import com.example.icsproject2easenetics.ui.screens.WisdomSharingScreen
import com.example.icsproject2easenetics.ui.screens.MfaVerificationScreen
import com.example.icsproject2easenetics.ui.screens.ModuleLessonsScreen
import com.example.icsproject2easenetics.ui.screens.ModulesScreen
import com.example.icsproject2easenetics.ui.screens.OnboardingScreen
import com.example.icsproject2easenetics.ui.screens.ProgressScreen
import com.example.icsproject2easenetics.ui.screens.ProfileScreen
import com.example.icsproject2easenetics.ui.screens.WisdomPostDetailScreen
import com.example.icsproject2easenetics.ui.screens.CreateWisdomPostScreen
import com.example.icsproject2easenetics.ui.screens.QuizScreen
import com.example.icsproject2easenetics.ui.screens.RegisterScreen
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.QuizViewModel

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

        composable("debug") {
            DebugScreen(
                onBack = { navController.popBackStack() }
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
                },
                onWisdomSharingClick = {
                    navController.navigate("wisdom_sharing")
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

        composable("wisdom_sharing") {
            val authViewModel: AuthViewModel = viewModel()
            val currentUser by authViewModel.currentUser.collectAsState()

            WisdomSharingScreen(
                onBack = { navController.popBackStack() },
                onCreatePost = {
                    navController.navigate("create_wisdom_post")
                },
                onPostClick = { postId ->
                    navController.navigate("wisdom_post_detail/$postId")
                },
                currentUserId = currentUser?.uid ?: "",
                currentUserName = extractUserName(currentUser) // This function exists in DashboardScreen
            )
        }

        composable("create_wisdom_post") {
            val authViewModel: AuthViewModel = viewModel()
            val currentUser by authViewModel.currentUser.collectAsState()

            CreateWisdomPostScreen(
                onBack = { navController.popBackStack() },
                onPostCreated = {
                    navController.popBackStack()
                    // Optionally show a success message or refresh
                },
                currentUserId = currentUser?.uid ?: "",
                currentUserName = extractUserName(currentUser),
                currentUserEmail = currentUser?.email ?: ""
            )
        }

        composable(
            "wisdom_post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val authViewModel: AuthViewModel = viewModel()
            val currentUser by authViewModel.currentUser.collectAsState()
            val postId = backStackEntry.arguments?.getString("postId") ?: ""

            WisdomPostDetailScreen(
                postId = postId,
                onBack = { navController.popBackStack() },
                currentUserId = currentUser?.uid ?: "",
                currentUserName = extractUserName(currentUser)
            )
        }

        // Modules Screen
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

        // Module Lessons Screen
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
                onStartQuiz = { quizLessonId ->
                    navController.navigate("quiz/$quizLessonId")
                },
                onMarkComplete = {
                    navController.popBackStack()
                }
            )
        }

        // Quiz Screen with parameter - FIXED VERSION
        composable(
            "quiz/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""

            // Use the QuizViewModel
            val quizViewModel: QuizViewModel = viewModel()

            // Load questions when screen opens
            LaunchedEffect(lessonId) {
                quizViewModel.loadQuizQuestions(lessonId)
            }

            // FIXED: Use collectAsState() to convert StateFlow to Compose State
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
                    Text(
                        text = errorMessage ?: "Failed to load quiz",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
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
                        // Save progress to Firebase in real implementation
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}