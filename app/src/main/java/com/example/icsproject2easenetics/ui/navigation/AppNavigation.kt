package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icsproject2easenetics.ui.screens.DashboardScreen
import com.example.icsproject2easenetics.ui.screens.OnboardingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate("dashboard")
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onLessonClick = { lessonId ->
                    // Will implement in Sprint 7
                    println("Lesson clicked: $lessonId")
                },
                onChatbotClick = {
                    // Will implement in Sprint 8
                    println("Chatbot clicked")
                },
                onProfileClick = {
                    // Will implement later
                    println("Profile clicked")
                }
            )
        }
    }
}