package com.neza.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neza.app.ui.chat.ChatScreen
import com.neza.app.ui.home.HomeScreen
import com.neza.app.ui.onboarding.OnboardingScreen
import com.neza.app.ui.settings.SettingsScreen

object NezaDestinations {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val CHAT = "chat"
    const val SETTINGS = "settings"
}

@Composable
fun NezaNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NezaDestinations.ONBOARDING) {
        composable(NezaDestinations.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(NezaDestinations.HOME) {
                        popUpTo(NezaDestinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(NezaDestinations.HOME) {
            HomeScreen(
                onOpenChat = { navController.navigate(NezaDestinations.CHAT) },
                onOpenSettings = { navController.navigate(NezaDestinations.SETTINGS) }
            )
        }
        composable(NezaDestinations.CHAT) {
            ChatScreen(onBack = { navController.popBackStack() })
        }
        composable(NezaDestinations.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
