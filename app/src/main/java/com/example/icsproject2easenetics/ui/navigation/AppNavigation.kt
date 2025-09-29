package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icsproject2easenetics.ui.screens.AuthenticatorSetupScreen
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.LoginScreen
import com.example.icsproject2easenetics.ui.screens.MfaVerificationScreen
import com.example.icsproject2easenetics.ui.screens.OnboardingScreen
import com.example.icsproject2easenetics.ui.screens.ProfileScreen
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
                    // Check if MFA verification is required
                    val currentUser = authViewModel.currentUser.value
                    currentUser?.let { user ->
                        // In a real app, you would check the user's MFA status from Firestore
                        // For now, we'll check the MFA verification state
                        if (authViewModel.mfaVerificationState.value is com.example.icsproject2easenetics.ui.viewmodels.MfaVerificationState.Required) {
                            navController.navigate("mfa_verification")
                        } else {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    } ?: run {
                        // If no user, go to dashboard directly
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
                    println("Lesson clicked: $lessonId")
                },
                onChatbotClick = {
                    println("Chatbot clicked")
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
                    // Optionally show success message or navigate to profile
                },
                onBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
    }
}