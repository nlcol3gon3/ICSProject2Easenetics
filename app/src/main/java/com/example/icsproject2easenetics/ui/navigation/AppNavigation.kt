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
import com.example.icsproject2easenetics.ui.screens.ProfileScreen
import com.example.icsproject2easenetics.ui.screens.RegisterScreen
import com.example.icsproject2easenetics.ui.viewmodels.AuthViewModel
import com.example.icsproject2easenetics.ui.viewmodels.UserViewModel

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
                    // Navigate to lesson screen with lessonId as parameter
                    navController.navigate("lesson/$lessonId")
                },
                onChatbotClick = {
                    navController.navigate("chatbot")
                },
                onProfileClick = {
                    navController.navigate("profile")
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

        // Chatbot Screen - NEW
        composable("chatbot") {
            ChatbotScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Lesson Screen - NEW with parameter
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
                onMarkComplete = {
                    // Optionally show confirmation or navigate back
                    navController.popBackStack()
                }
            )
        }
    }
}