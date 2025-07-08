package com.example.capstone_404.navigation

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
        // 임시 연결 상태
        composable(Route.PROFILE_INPUT) {
            ProfileInputScreen(
                onStartClicked = {
                    navController.navigate(Route.PROFILE_INPUT)
                }
            )
        }
    }
}