package com.example.icsproject2easenetics.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icsproject2easenetics.data.models.User
import com.example.icsproject2easenetics.ui.screens.*

@Composable
fun AppNavigation(currentUser: User?) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "dashboard" else "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onGetStarted = { navController.navigate("dashboard") }
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
                }
            )
        }

        composable("lesson/{lessonId}") { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonScreen(
                lessonId = lessonId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("chatbot") {
            ChatbotScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}