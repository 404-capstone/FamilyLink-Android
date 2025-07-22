package com.example.capstone_404.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.capstone_404.ui.component.BottomNavigationBar
import com.example.capstone_404.ui.component.bottomTabs
import com.example.capstone_404.ui.group.GroupScreen
import com.example.capstone_404.ui.login.LoginScreen
import com.example.capstone_404.ui.login.ProfileInputScreen

// 페이지 만들 때 추가 해야됨
@Composable
fun AppNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomTabs.map { it.route }) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 로그인
            composable(Route.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Route.PROFILE_INPUT)
                    }
                )
            }
            // 추가 정보 입력
            composable(Route.PROFILE_INPUT) {
                ProfileInputScreen(
                    onStartClicked = {
                        navController.navigate(Route.GROUP)
                    }
                )
            }
            // 그룹 메인
            composable(Route.GROUP) {
                GroupScreen(
                )
            }

            // 임시 정의
            composable(Route.CALENDAR) {  }
            composable(Route.DIARY) {  }
            composable(Route.ALBUM) {  }
            composable(Route.MYPAGE) {  }
        }
    }
}