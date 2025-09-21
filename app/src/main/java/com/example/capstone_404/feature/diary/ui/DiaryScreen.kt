package com.example.capstone_404.feature.diary.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.DiaryState
import com.example.capstone_404.feature.diary.ui.component.DiaryTabRow
import com.example.capstone_404.feature.diary.ui.content.DiaryJoinedContent
import com.example.capstone_404.feature.diary.viewmodel.DiaryViewModel
import com.example.capstone_404.ui.component.NotJoinedGroupContent
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.component.fab.SingleFab
import com.example.capstone_404.ui.theme.Background

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel(),
    onNavigateToWrite: () -> Unit = {},
    onNavigateToGroup: () -> Unit = {},
    onNavigateToDiarySelect: (String) -> Unit = {},
    onNavigateToQuestionSelect: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState(initial = DiaryState.Success(emptyList(), emptyList()))
    val selectedTab by viewModel.selectedTab.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val groupInfo by viewModel.groupInfoFlow.collectAsState(initial = null)

    LaunchedEffect(groupInfo) {
        if (groupInfo != null) {
            viewModel.loadUserData()
        }
    }

    // 에러 메시지 처리
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "다이어리",
                navigationType = NavigationType.NONE
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (groupInfo != null) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val horizontalPadding = when {
                            maxWidth < 400.dp -> 16.dp
                            maxWidth < 600.dp -> 24.dp
                            else -> 40.dp
                        }

                        when (val currentState = state) {
                            DiaryState.Loading -> {
                                LoadingDialog("데이터를 불러오는 중...\n잠시만 기다려주세요!")
                            }

                            is DiaryState.Success -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(start = horizontalPadding, end = horizontalPadding)
                                    ) {
                                        // 탭 영역
                                        DiaryTabRow(
                                            selectedTab = selectedTab,
                                            onTabSelected = { tab ->
                                                viewModel.switchTab(tab)
                                            }
                                        )

                                        // 가입 상태
                                        DiaryJoinedContent(
                                            selectedTab = selectedTab,
                                            diaryEntries = currentState.diaryEntries,
                                            questions = currentState.questions,
                                            onDiaryClick = { diary -> onNavigateToDiarySelect(diary.id) },
                                            onQuestionClick = { question -> onNavigateToQuestionSelect(question.id) }
                                        )
                                    }
                                    SingleFab(
                                        icon = R.drawable.ic_edit,
                                        onClick = {
                                            viewModel.checkCanWriteDiary(
                                                onCanWrite = onNavigateToWrite,
                                                onAlreadyWritten = {
                                                    Toast.makeText(
                                                        context,
                                                        "오늘 이미 다이어리를 작성하셨습니다.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                },
                                                onError = {
                                                    Toast.makeText(
                                                        context,
                                                        "알 수 없는 오류가 발생했습니다.\n다시 시도해주세요.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    NotJoinedGroupContent(
                        onNavigateToGroup = onNavigateToGroup
                    )
                }
            }
        }
    }
}