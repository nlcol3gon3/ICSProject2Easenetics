package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.LoginScreen
import com.example.icsproject2easenetics.ui.screens.OnboardingScreen
import com.example.icsproject2easenetics.ui.screens.RegisterScreen
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
                    // Check if user is already logged in
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
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
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
                    println("Lesson clicked: $lessonId")
                },
                onChatbotClick = {
                    println("Chatbot clicked")
                },
                onProfileClick = {
                    println("Profile clicked")
                }
            )
        }
    }
}