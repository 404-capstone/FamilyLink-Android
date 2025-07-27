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
import com.example.capstone_404.feature.group.ui.GroupScreen
import com.example.capstone_404.feature.group.ui.RoleSelectScreen
import com.example.capstone_404.feature.group.ui.SurveyIntroScreen
import com.example.capstone_404.feature.login.ui.LoginScreen
import com.example.capstone_404.feature.login.ui.ProfileInputScreen
import com.example.capstone_404.feature.login.ui.SplashScreen

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
            // UI 빌드 테스트 할 때 startDestination = Route.{테스트 UI 경로}로 바꿔서 테스트하고 다시 LOGIN으로 돌려놓으면 됨
            startDestination = Route.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 스플래시(자동 로그인)
            composable(Route.SPLASH) {
                SplashScreen(
                    onLoggedIn = { navController.navigate(Route.GROUP) { popUpTo("splash") { inclusive = true } } },
                    onNotLoggedIn = { navController.navigate(Route.LOGIN) { popUpTo("splash") { inclusive = true } } }
                )
            }
            // 로그인
            composable(Route.LOGIN) {
                LoginScreen(
                    onNavigateToHome = { navController.navigate(Route.GROUP) { popUpTo("login") { inclusive = true } } },
                    onNavigateToProfileInput = { navController.navigate(Route.PROFILE_INPUT) { popUpTo("login") { inclusive = true } } }
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
                    onCreate = {
                        navController.navigate(Route.ROLE_SELECT)
                    }
                )
            }
            // 그룹 역할 선택
            composable(Route.ROLE_SELECT) {
                RoleSelectScreen(
                    onSubmit = {}
                )
            }
            // 가입 완료 문구 + 설문 안내
            composable(Route.SURVEY_INTRO) {
                SurveyIntroScreen()
            }

            // 임시 정의
            composable(Route.CALENDAR) {  }
            composable(Route.DIARY) {  }
            composable(Route.ALBUM) {  }
            composable(Route.MYPAGE) {  }
        }
    }
}