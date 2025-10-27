package com.example.capstone_404.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.capstone_404.feature.album.ui.AlbumScreen
import com.example.capstone_404.feature.album.ui.PhotoDetailScreen
import com.example.capstone_404.feature.album.ui.PhotoEditScreen
import com.example.capstone_404.feature.album.ui.PhotoInputScreen
import com.example.capstone_404.feature.album.viewmodel.AlbumViewModel
import com.example.capstone_404.feature.calendar.model.schedule.recommend.RecommendResultUiState
import com.example.capstone_404.feature.calendar.ui.ActivityRecommendScreen
import com.example.capstone_404.feature.calendar.ui.AreaSelectionScreen
import com.example.capstone_404.feature.calendar.ui.CalendarScreen
import com.example.capstone_404.feature.diary.ui.DiaryScreen
import com.example.capstone_404.feature.diary.ui.DiarySelectScreen
import com.example.capstone_404.feature.diary.ui.DiaryWriteScreen
import com.example.capstone_404.feature.diary.ui.QuestionSelectScreen
import com.example.capstone_404.feature.calendar.ui.FamilyScheduleScreen
import com.example.capstone_404.feature.calendar.ui.PersonalScheduleScreen
import com.example.capstone_404.feature.calendar.ui.RecommendLoadingScreen
import com.example.capstone_404.feature.calendar.ui.RecommendResultScreen
import com.example.capstone_404.feature.calendar.ui.ScheduleDetailScreen
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
import com.example.capstone_404.navigation.CalendarNavKeys.SELECTED_AREA
import com.example.capstone_404.session.AppSharedViewModel
import com.example.capstone_404.session.LogoutReason
import java.net.URLDecoder

// 페이지 만들 때 추가 해야됨
@Composable
fun AppNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val appSharedViewModel: AppSharedViewModel = hiltViewModel()

    // 로그아웃 이벤트 감지 후 처리
    LaunchedEffect(Unit) {
        appSharedViewModel.logoutEvents.collect { reason ->
            if (reason == LogoutReason.ExpiredRefresh) {
                navController.navigate(Route.LOGIN) {
                    popUpTo(0) { inclusive = true}
                }
            }
        }
    }

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
                    onNavigateToDetail = { schedule ->
                        navController.navigate("schedule_detail/scheduleId=${schedule.id}?writerId=${schedule.writerId ?: -1}")
                    },
                    onNavigateToGroupActivity = {
                        viewModel.presetRecommendFromSelectedDate()
                        navController.navigate(Route.ACTIVITY_RECOMMEND) { popUpTo("calendar") { inclusive = false } }
                    },
                    onNavigateToGroupScheduleAdd = {
                        viewModel.presetFamilyFromSelectedDate()
                        navController.navigate("schedule_family?formMode=add") { popUpTo("calendar") { inclusive = false } }
                    },
                    onNavigateToPersonalScheduleAdd = {
                        viewModel.presetPersonalFromSelectedDate()
                        navController.navigate("schedule_personal?formMode=add") { popUpTo("calendar") { inclusive = false } }
                    },
                )
            }
            // 일정 상세 조회
            composable(
                route = Route.SCHEDULE_DETAIL,
                arguments = listOf(
                    navArgument("scheduleId") { type = NavType.IntType },
                    navArgument("writerId") { type = NavType.IntType; defaultValue = -1 }
                )
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                val scheduleId = backStackEntry.arguments?.getInt("scheduleId")!!
                val writerIdArg = backStackEntry.arguments?.getInt("writerId") ?: -1
                val writerId = if (writerIdArg == -1) null else writerIdArg
                ScheduleDetailScreen(
                    viewModel = viewModel,
                    scheduleId = scheduleId,
                    writerId = writerId,
                    onBack = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onPersonalEdit = {
                        navController.navigate("schedule_personal?formMode=edit")
                    },
                    onFamilyEdit = {
                        navController.navigate("schedule_family?formMode=edit")
                    }
                )
            }
            // 개인 일정 추가|수정
            composable(
                route = Route.SCHEDULE_PERSONAL,
                arguments = listOf(
                    navArgument("formMode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                val formMode = backStackEntry.arguments!!.getString("formMode") ?: "add"
                PersonalScheduleScreen(
                    viewModel = viewModel,
                    formMode = formMode,
                    onClose = { navController.popBackStack() },
                    onAdd = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onEdit = { scheduleId, writerId ->
                        navController.navigate("schedule_detail/scheduleId=${scheduleId}?writerId=${writerId}") { popUpTo("calendar") { inclusive = false } }
                    }
                )
            }
            // 가족 일정 추가|수정
            composable(
                route = Route.SCHEDULE_FAMILY,
                arguments = listOf(
                    navArgument("formMode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                val formMode = backStackEntry.arguments!!.getString("formMode") ?: "add"
                FamilyScheduleScreen(
                    viewModel = viewModel,
                    formMode = formMode,
                    onClose = { navController.popBackStack() },
                    onAdd = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onEdit = {
                        navController.navigate("schedule_detail/scheduleId=${it}") { popUpTo("calendar") { inclusive = false } }
                    }
                )
            }
            // 가족 활동 추천
            composable(Route.ACTIVITY_RECOMMEND) {backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)

                LaunchedEffect(Unit) {
                    val handle = navController.currentBackStackEntry?.savedStateHandle ?: return@LaunchedEffect
                    handle.getStateFlow<String?>(SELECTED_AREA, null).collect { fullName ->
                        if (fullName != null) {
                            viewModel.setRecommendArea(fullName)
                            handle[SELECTED_AREA] = null
                        }
                    }
                }
                ActivityRecommendScreen(
                    viewModel = viewModel,
                    onBack = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onClickArea = {
                        navController.navigate(Route.AREA_SELECT)
                    },
                    onRecommend = {
                        navController.navigate(Route.RECOMMEND_LOADING) { popUpTo("calendar") { inclusive = false } }
                    },
                )
            }
            // 활동 추천 지역 선택
            composable(Route.AREA_SELECT) {
                AreaSelectionScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onConfirm = { fullName ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(SELECTED_AREA, fullName)

                        navController.popBackStack()
                    },
                )
            }
            // 활동 추천 로딩
            composable(Route.RECOMMEND_LOADING) {backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                RecommendLoadingScreen(
                    viewModel = viewModel,
                    onClose = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onNavigateToResult = {
                        navController.navigate(Route.RECOMMEND_RESULT) { popUpTo("calendar") { inclusive = false } }
                    }
                )
            }
            // 활동 추천 결과
            composable(Route.RECOMMEND_RESULT) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.CALENDAR)
                }
                val viewModel: CalendarViewModel = hiltViewModel(parentEntry)
                val result = viewModel.recommendResult.collectAsState().value as RecommendResultUiState.Success
                RecommendResultScreen(
                    viewModel = viewModel,
                    data = result.data,
                    onClose = {
                        navController.navigate(Route.CALENDAR) { popUpTo(0) { inclusive = true } }
                    },
                    onAddSchedules = {
                        viewModel.presetFamilyFromSelectedDate()
                        navController.navigate("schedule_family?formMode=add") { popUpTo("calendar") { inclusive = false } }
                    }
                )
            }

            //내 정보
            composable(Route.MYPAGE) {
                MyPageScreen(
                    onProfileEdit = {
                        navController.navigate(Route.PROFILE_EDIT)
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

            // 앨범
            composable(Route.ALBUM) { backStackEntry ->
                val viewModel: AlbumViewModel = hiltViewModel(backStackEntry)
                AlbumScreen(
                    viewModel = viewModel,
                    onNavigateToGroup = {
                        navController.navigate(Route.GROUP) {
                            popUpTo(Route.ALBUM) { inclusive = true }
                        }
                    },
                    onNavigateToPhotoInput = { encodedImageUri ->
                        navController.navigate("photo_input/$encodedImageUri")
                    },
                    onNavigateToPhotoDetail = { photoId ->
                        navController.navigate("photo_detail/$photoId")
                    }
                )
            }

            // 사진 정보 입력
            composable(
                route = Route.PHOTO_INPUT,
                arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.ALBUM)
                }
                val viewModel: AlbumViewModel = hiltViewModel(parentEntry)

                val encodedImageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
                val imageUri = URLDecoder.decode(encodedImageUri, "UTF-8").toUri()

                PhotoInputScreen(
                    viewModel = viewModel,
                    initialImageUri = imageUri,
                    onNavigateBack = {
                        navController.popBackStack(Route.ALBUM, inclusive = false)
                    },
                    onSaveComplete = {
                        navController.popBackStack(Route.ALBUM, inclusive = false)
                    }
                )
            }

            // 사진 상세 조회
            composable(
                route = Route.PHOTO_DETAIL,
                arguments = listOf(navArgument("photoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.ALBUM)
                }
                val viewModel: AlbumViewModel = hiltViewModel(parentEntry)

                val photoId = backStackEntry.arguments?.getString("photoId") ?: ""

                PhotoDetailScreen(
                    viewModel = viewModel,
                    photoId = photoId,
                    onNavigateBack = {
                        navController.popBackStack(Route.ALBUM, inclusive = false)
                    },
                    onNavigateToEdit = { editPhotoId ->
                        navController.navigate("photo_edit/$editPhotoId")
                    }
                )
            }

            // 사진 정보 수정
            composable(
                route = Route.PHOTO_EDIT,
                arguments = listOf(navArgument("photoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.ALBUM)
                }
                val viewModel: AlbumViewModel = hiltViewModel(parentEntry)

                val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
                val photo = viewModel.getPhotoById(photoId)

                if (photo != null) {
                    PhotoEditScreen(
                        viewModel = viewModel,
                        photo = photo,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}