package com.example.capstone_404.feature.diary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.ui.component.ResultSectionCard
import com.example.capstone_404.feature.diary.ui.component.DiaryContentSection
import com.example.capstone_404.feature.diary.ui.component.EmotionAnalysisSection
import com.example.capstone_404.feature.diary.ui.component.AiFeedbackSection
import com.example.capstone_404.feature.diary.viewmodel.DiarySelectViewModel
import com.example.capstone_404.feature.diary.model.DiaryDetail
import com.example.capstone_404.feature.diary.model.DiarySelectUiState
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun DiarySelectScreen(
    navController: NavController,
    diaryId: String,
    viewModel: DiarySelectViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    val diarySelectState by viewModel.diarySelectState.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }
    
    LaunchedEffect(diaryId) {
        viewModel.selectDiary(diaryId)
    }
    
    LaunchedEffect(diarySelectState) {
        if (diarySelectState is DiarySelectUiState.Deleted) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "다이어리 상세",
                navigationType = NavigationType.BACK,
                onNavigationClick = onNavigateBack,
                rightButton = {
                    IconButton(onClick = { showDeleteDialog.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_withdraw),
                            contentDescription = "삭제",
                            tint = TextBlack
                        )
                    }
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 16.dp
                maxWidth < 600.dp -> 24.dp
                else -> 40.dp
            }

            when (val state = diarySelectState) {
                is DiarySelectUiState.Success -> {
                    val detail = state.detail
                    if (detail is DiaryDetail) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = horizontalPadding, end = horizontalPadding)
                        ) {
                            item {
                                Text(
                                    text = detail.date,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextBlack,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }
                            item {
                                ResultSectionCard(
                                    title = "다이어리 내용",
                                    modifier = Modifier.fillMaxWidth(),
                                    content = { DiaryContentSection(text = detail.diaryText) }

                                )
                            }
                            item {
                                ResultSectionCard(
                                    title = "감정 분석",
                                    content = { EmotionAnalysisSection(emotions = detail.emotions) }
                                )
                            }
                            item {
                                ResultSectionCard(
                                    title = "AI 피드백",
                                    modifier = Modifier.fillMaxWidth(),
                                    content = { AiFeedbackSection(feedback = detail.aiFeedback) }
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = horizontalPadding, end = horizontalPadding),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "잘못된 데이터 형식입니다.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBlack
                            )
                        }
                    }
                }
                is DiarySelectUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = horizontalPadding, end = horizontalPadding)
                    ) {
                        item {
                            Text(
                                text = "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBlack,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    }
                }
                is DiarySelectUiState.Deleted -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = horizontalPadding, end = horizontalPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "삭제되었습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextBlack
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        ActionDialog(
            title = "해당 다이어리를 삭제하시겠습니까?",
            description = "작성된 내용 및 피드백 결과는 삭제되며 복구할 수 없습니다.",
            confirmText = "삭제",
            onConfirm = {
                showDeleteDialog.value = false
                viewModel.deleteDiary(diaryId)
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }
}


