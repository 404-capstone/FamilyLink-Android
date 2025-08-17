package com.example.capstone_404.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.capstone_404.feature.calendar.ui.CalendarScreen
import com.example.capstone_404.feature.diary.ui.DiaryScreen
import com.example.capstone_404.feature.diary.ui.DiarySelectScreen
import com.example.capstone_404.feature.diary.ui.DiaryWriteScreen
import com.example.capstone_404.feature.diary.ui.QuestionSelectScreen
import com.example.capstone_404.feature.calendar.ui.FamilyScheduleScreen
import com.example.capstone_404.feature.calendar.ui.PersonalScheduleScreen
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.bar.BottomNavigationBar
import com.example.capstone_404.ui.component.bar.bottomTabs
import com.example.capstone_404.feature.group.ui.GroupScreen
import com.example.capstone_404.feature.group.ui.InviteCodeScreen
import com.example.capstone_404.feature.group.ui.RoleSelectScreen
import com.example.capstone_404.feature.group.ui.SurveyIntroScreen
import com.example.capstone_404.feature.group.ui.SurveyListScreen
import com.example.capstone_404.feature.group.ui.SurveyResultScreen
import com.example.capstone_404.feature.login.ui.LoginScreen
import com.example.capstone_404.feature.login.ui.ProfileInputScreen
import com.example.capstone_404.feature.login.ui.SplashScreen
import com.example.capstone_404.feature.mypage.ui.MyPageScreen
import com.example.capstone_404.feature.mypage.ui.ProfileEditScreen

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
                            popUpTo("group") {
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
                    onNavigateToHome = { navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } } },
                    onNavigateToProfileInput = { navController.navigate(Route.PROFILE_INPUT) { popUpTo(0) { inclusive = true } } },
                    onNotLoggedIn = { navController.navigate(Route.LOGIN) { popUpTo(0) { inclusive = true } } }
                )
            }
            // 로그인
            composable(Route.LOGIN) {
                LoginScreen(
                    onNavigateToHome = { navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } } },
                    onNavigateToProfileInput = { navController.navigate(Route.PROFILE_INPUT) { popUpTo(0) { inclusive = true } } }
                )
            }
            // 추가 정보 입력
            composable(Route.PROFILE_INPUT) {
                ProfileInputScreen(
                    onNavigateToHome = {
                        navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
            // 그룹 메인
            composable(Route.GROUP) {
                GroupScreen(
                    onCreate = { groupName, encodedUri ->
                        navController.navigate("roleSelect?groupName=$groupName&imageUri=$encodedUri")
                    },
                    onJoin = { inviteCode ->
                        navController.navigate("roleSelect?inviteCode=$inviteCode")
                    },
                    onNavigateToWrite = {
                        navController.navigate("survey_list") { popUpTo("group") { inclusive = false } }
                    },
                    onNavigateToResult = {
                        navController.navigate("survey_result") { popUpTo("group") { inclusive = false } }
                    },
                    onNavigateToInvite = {
                        navController.navigate("invite_code") { popUpTo("group") { inclusive = false } }
                    }
                )
            }
            // 그룹 역할 선택
            composable(
                route = Route.ROLE_SELECT,
                arguments = listOf(
                    navArgument("groupName") { type = NavType.StringType; defaultValue = "" },
                    navArgument("imageUri") { type = NavType.StringType; defaultValue = "" },
                    navArgument("inviteCode") { type = NavType.StringType; defaultValue = "" }
                )
            ) {
                RoleSelectScreen(
                    onSubmit = {
                        navController.navigate(Route.SURVEY_INTRO) { popUpTo("group") { inclusive = false } }
                    }
                )
            }
            // 가입 완료 문구 + 설문 안내
            composable(Route.SURVEY_INTRO) {
                SurveyIntroScreen(
                    onNextPage = {
                        navController.navigate(Route.SURVEY_LIST) { popUpTo("group") { inclusive = false } }
                    }
                )
            }
            // 설문지
            composable(Route.SURVEY_LIST) {
                SurveyListScreen(
                    onSubmitComplete = {
                        navController.navigate(Route.SURVEY_RESULT) { popUpTo("group") { inclusive = false } }
                    },
                    onClickToBack = {
                        navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
            // 설문 결과
            composable(Route.SURVEY_RESULT) {
                SurveyResultScreen(
                    onClickGoHome = {
                        navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
            // 그룹원 초대
            composable(Route.INVITE_CODE) {
                InviteCodeScreen(
                    onClickToBack = {
                        navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            // 캘린더 메인
            composable(Route.CALENDAR) { backStackEntry ->
                val viewModel: CalendarViewModel = hiltViewModel(backStackEntry)
                CalendarScreen(
                    onNavigateToGroup = {
                        navController.navigate(Route.GROUP) { popUpTo(0) { inclusive = true } }
                        },
                    onNavigateToGroupActivity = {},
                    onNavigateToGroupScheduleAdd = {
                        viewModel.presetFamilyFromSelectedDate()
                        navController.navigate(Route.ADD_FAMILY) { popUpTo("calendar") { inclusive = false } }
                    },
                    onNavigateToPersonalScheduleAdd = {
                        viewModel.presetPersonalFromSelectedDate()
                        navController.navigate(Route.ADD_PERSONAL) { popUpTo("calendar") { inclusive = false } }
                    },
                )
            }
            // 개인 일정 추가
            composable(Route.ADD_PERSONAL) {backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                PersonalScheduleScreen(
                    viewModel = viewModel,
                    onClose = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onSubmit = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    }
                )
            }
            // 가족 일정 추가
            composable(Route.ADD_FAMILY) {
                    backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                FamilyScheduleScreen(
                    viewModel = viewModel,
                    onClose = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onSubmit = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    }
                )
            }

            //내 정보
            composable(Route.MYPAGE) {
                MyPageScreen(
                    onProfileEdit = {
                        navController.navigate(Route.PROFILE_EDIT)
                    },
                    onInquiry = {
                        // TODO: 문의하기 화면으로 이동 또는 외부 링크
                    },
                    onWithdraw = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onLogout = {
                        navController.navigate(Route.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 프로필 편집
            composable(Route.PROFILE_EDIT) {
                ProfileEditScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSave = {
                        navController.popBackStack()
                    }
                )
            }

            // 다이어리
            composable(Route.DIARY) {
                DiaryScreen(
                    onNavigateToWrite = {
                        navController.navigate(Route.DIARY_WRITE)
                    },
                    onNavigateToGroup = {
                        navController.navigate(Route.GROUP) {
                            popUpTo(Route.DIARY) { inclusive = true }
                        }
                    },
                    onNavigateToDiarySelect = { diaryId ->
                        navController.navigate(Route.DIARY_SELECT.replace("{diaryId}", diaryId))
                    },
                    onNavigateToQuestionSelect = { questionId ->
                        navController.navigate(Route.QUESTION_SELECT.replace("{questionId}", questionId))
                    }
                )
            }

            composable(Route.DIARY_WRITE) {
                DiaryWriteScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 다이어리 상세 조회
            composable(
                route = Route.DIARY_SELECT,
                arguments = listOf(navArgument("diaryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val diaryId = backStackEntry.arguments?.getString("diaryId") ?: ""
                DiarySelectScreen(
                    navController = navController,
                    diaryId = diaryId
                )
            }

            // 공통 질문 상세 조회
            composable(
                route = Route.QUESTION_SELECT,
                arguments = listOf(navArgument("questionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
                QuestionSelectScreen(
                    navController = navController,
                    questionId = questionId
                )
            }

            // 임시 정의
            composable(Route.ALBUM) {  }

        }
    }
}