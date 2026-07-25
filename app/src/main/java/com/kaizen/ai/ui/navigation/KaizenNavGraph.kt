package com.kaizen.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kaizen.ai.ui.chat.ChatScreen
import com.kaizen.ai.ui.home.HomeScreen
import com.kaizen.ai.ui.onboarding.OnboardingScreen
import com.kaizen.ai.ui.settings.SettingsScreen

object KaizenDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
}

@Composable
fun KaizenNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = KaizenDestinations.ONBOARDING) {
        composable(KaizenDestinations.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(KaizenDestinations.HOME) {
                        popUpTo(KaizenDestinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(KaizenDestinations.HOME) {
            HomeScreen(
                onOpenChat = { navController.navigate(KaizenDestinations.CHAT) },
                onOpenSettings = { navController.navigate(KaizenDestinations.SETTINGS) }
            )
        }
        composable(KaizenDestinations.CHAT) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
        composable(KaizenDestinations.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
