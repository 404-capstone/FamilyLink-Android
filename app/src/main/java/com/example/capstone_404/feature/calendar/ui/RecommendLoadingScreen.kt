package com.example.capstone_404.feature.calendar.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.capstone_404.feature.calendar.model.schedule.recommend.RecommendResultUiState
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.component.AiMascot
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack
import java.time.ZoneId

@Composable
fun RecommendLoadingScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onNavigateToResult: () -> Unit,
) {
    val context = LocalContext.current
    val zoneId = remember { ZoneId.systemDefault() }
    val state by viewModel.recommendResult.collectAsState()

    var showCancelDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !showCancelDialog) {
        showCancelDialog = true
    }
    if (showCancelDialog) {
        BackHandler(enabled = true) {
            showCancelDialog = false
        }
    }
    // 초기 진입 시 추천 시작
    LaunchedEffect(Unit) {
        viewModel.requestRecommend(zoneId)
    }
    // 추천 결과에 따른 처리
    LaunchedEffect(state) {
        when (state) {
            is RecommendResultUiState.Success -> onNavigateToResult()
            is RecommendResultUiState.Error -> {
                Toast.makeText(context, (state as RecommendResultUiState.Error).message, Toast.LENGTH_SHORT).show()
                onClose()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "활동 추천",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = { showCancelDialog = true }
            )
            if (showCancelDialog) {
                ActionDialog(
                    title = "활동 추천을 취소하시겠습니까?",
                    description = "활동 추천에 입력된 정보는 모두 삭제됩니다.",
                    confirmText = "추천 취소",
                    cancelText = "아니오",
                    onConfirm = {
                        showCancelDialog = false
                        onClose()
                    },
                    onDismiss = { showCancelDialog = false }
                )
            }
        }
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
                maxHeight < 600.dp -> 24.dp
                maxHeight < 800.dp -> 32.dp
                else -> 40.dp
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // AI 캐릭터
                AiMascot()
                // 진행 문구
                Text(
                    text = "가족에게 어울리는 활동을 찾고 있어요!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )
                Spacer(Modifier.height(20.dp))
                // 안내 문구
                GuideWarningCard("뒤로가기 또는 앱 종료 시 활동 추천이 취소됩니다.")
            }
        }
    }
}