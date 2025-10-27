package com.example.capstone_404.feature.mypage.ui

import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.mypage.ui.component.AccountManagement
import com.example.capstone_404.feature.mypage.ui.component.Settings
import com.example.capstone_404.feature.mypage.ui.component.UserInfoCard
import com.example.capstone_404.feature.mypage.ui.dialog.InquiryDialog
import com.example.capstone_404.feature.mypage.ui.dialog.WithdrawDialog
import com.example.capstone_404.feature.mypage.viewmodel.LogoutState
import com.example.capstone_404.feature.mypage.viewmodel.MyPageViewModel
import com.example.capstone_404.feature.mypage.viewmodel.WithdrawState
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.Background

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel = hiltViewModel(),
    onProfileEdit: () -> Unit = {},
    onWithdraw: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    // 상태 관찰
    val nickname by viewModel.nicknameFlow.collectAsState(initial = "사용자")
    val socialProvider by viewModel.socialProviderFlow.collectAsState(initial = null)
    val profileImage by viewModel.profileImageFlow.collectAsState(initial = null)
    val logoutState by viewModel.logoutState.collectAsState()
    val withdrawState by viewModel.withdrawState.collectAsState()
    val isAlarmEnabled by viewModel.alarmEnabledFlow.collectAsState(initial = true)
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    // 다이얼로그 상태
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showInquiryDialog by remember { mutableStateOf(false) }

    // 화면 진입 시 사용자 정보 로드
    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    // 에러 메시지 처리
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // 로그아웃 성공 시 콜백
    LaunchedEffect(logoutState) {
        if (logoutState is LogoutState.Success) {
            onLogout()
            viewModel.resetLogoutState()
        }
    }

    // 회원 탈퇴 성공 시 콜백
    LaunchedEffect(withdrawState) {
        if (withdrawState is WithdrawState.Success) {
            onWithdraw()
            viewModel.resetWithdrawState()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "내 정보",
                navigationType = NavigationType.NONE
            )
        },
        containerColor = Background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 24.dp
                maxWidth < 600.dp -> 32.dp
                else -> 40.dp
            }

            val verticalPadding = when {
                maxHeight < 600.dp -> 16.dp
                maxHeight < 800.dp -> 24.dp
                else -> 32.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 사용자 정보 카드
                UserInfoCard(
                    nickname = nickname ?: "사용자",
                    socialProvider = socialProvider?.uppercase() ?: "",
                    profileImageUrl = profileImage,
                    onProfileEdit = onProfileEdit,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 설정
                Settings(
                    isNotificationEnabled = isAlarmEnabled,
                    onNotificationChange = { enabled ->
                        viewModel.setAlarmNotification(enabled)
                    },
                    onInquiry = { showInquiryDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 계정 관리
                AccountManagement(
                    onWithdraw = { showWithdrawDialog = true },
                    onLogout = { viewModel.logout() }
                )

                Spacer(modifier = Modifier.height(verticalPadding))
            }
        }
    }

    // 회원 탈퇴 확인 다이얼로그
    if (showWithdrawDialog) {
        WithdrawDialog(
            onConfirm = {
                showWithdrawDialog = false
                viewModel.withdraw()
            },
            onDismiss = {
                showWithdrawDialog = false
            }
        )
    }

    // 문의하기 다이얼로그
    if (showInquiryDialog) {
        InquiryDialog(
            onDismiss = {
                showInquiryDialog = false
            }
        )
    }
}