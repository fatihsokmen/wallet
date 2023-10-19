package com.github.fatihsokmen.wallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fatihsokmen.wallet.presentation.home.HomeScreen

@Composable
fun WalletAppApp(
    appState: WalletAppState = rememberWalletAppState()
) {
    if (appState.isOnline) {
        NavHost(
            navController = appState.navController,
            startDestination = Screen.Home.route
        ) {
            composable(Screen.Home.route) { _ ->
                HomeScreen()
            }
        }
    }
}

@Composable
fun rememberWalletAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    WalletAppState(navController, context)
}

class WalletAppState(
    val navController: NavHostController,
    private val context: Context
) {
    var isOnline by mutableStateOf(checkIfOnline())
        private set

    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    private fun checkIfOnline(): Boolean {
        val manager = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val capabilities = manager?.getNetworkCapabilities(manager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
}
