package com.example.capstone_404.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.capstone_404.ui.login.view.LoginScreen
import com.example.capstone_404.ui.login.view.ProfileInputScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.LOGIN
    ) {
        composable(Route.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.PROFILE_INPUT)
                }
            )
        }

        composable(Route.PROFILE_INPUT) {
            ProfileInputScreen()
        }
    }
}