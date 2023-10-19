package com.github.fatihsokmen.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fatihsokmen.wallet.presentation.home.HomeScreen

@Composable
fun WalletAppApp(
    appState: WalletAppState = rememberWalletAppState()
) {
    NavHost(
        navController = appState.navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { _ ->
            HomeScreen()
        }
    }
}

@Composable
fun rememberWalletAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    WalletAppState(navController)
}

class WalletAppState(
    val navController: NavHostController,
)

sealed class Screen(val route: String) {
    data object Home : Screen("home")
}
