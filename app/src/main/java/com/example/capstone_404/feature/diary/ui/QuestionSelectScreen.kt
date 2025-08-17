package com.example.capstone_404.feature.diary.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
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
import com.example.capstone_404.feature.diary.model.DiarySelectUiState
import com.example.capstone_404.feature.diary.model.QuestionDetail
import com.example.capstone_404.feature.diary.model.SelectType
import com.example.capstone_404.feature.diary.ui.component.QuestionCollapsedCard
import com.example.capstone_404.feature.diary.ui.component.QuestionExpandedCard
import com.example.capstone_404.feature.diary.viewmodel.DiarySelectViewModel
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack


@Composable
fun QuestionSelectScreen(
    navController: NavController,
    questionId: String,
    viewModel: DiarySelectViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    val diarySelectState by viewModel.diarySelectState.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }
    
    // 각 질문의 확장/축소 상태를 관리
    val expandedQuestions = remember { mutableStateOf(setOf<Int>()) }
    
    LaunchedEffect(questionId) {
        viewModel.selectQuestion(questionId)
    }
    
    LaunchedEffect(diarySelectState) {
        if (diarySelectState is DiarySelectUiState.Deleted) {
            navController.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            CustomTopBar(
                title = "공통질문 상세",
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
                    if (detail is QuestionDetail) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = horizontalPadding, end = horizontalPadding)
                        ) {
                            // 날짜 표시
                            item {
                                Text(
                                    text = detail.date,
                                    style = typography.headlineSmall,
                                    color = TextBlack,
                                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                                )
                            }
                            
                            // 질문 카드
                            items(detail.items.size) { idx ->
                                val item = detail.items[idx]
                                if (expandedQuestions.value.contains(idx)) {
                                    // 확장된 카드
                                    QuestionExpandedCard(
                                        index = idx,
                                        authorChips = item.question.responders,
                                        text = item.question.question,
                                        answers = item.answers,
                                        onClickCollapse = {
                                            expandedQuestions.value = expandedQuestions.value - idx
                                        }
                                    )
                                } else {
                                    // 축소된 카드
                                    QuestionCollapsedCard(
                                        index = idx,
                                        text = item.question.question,
                                        onClick = {
                                            expandedQuestions.value = expandedQuestions.value + idx
                                        }
                                    )
                                }
                                // 마지막 아이템이 아닌 경우 간격 추가
                                if (idx != detail.items.lastIndex) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(24.dp))
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
                                style = typography.bodyMedium,
                                color = TextBlack
                            )
                        }
                    }
                }
                is DiarySelectUiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = horizontalPadding, end = horizontalPadding),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "불러오는 중...",
                            style = typography.bodyMedium,
                            color = TextBlack,
                            modifier = Modifier.padding(top = 24.dp)
                        )
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
                            style = typography.bodyMedium,
                            color = TextBlack
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        ActionDialog(
            title = "해당 공통 질문을 삭제하시겠습니까?",
            description = "작성된 내용 및 피드백 결과는 삭제되며 복구할 수 없습니다.",
            confirmText = "삭제",
            onConfirm = {
                showDeleteDialog.value = false
                viewModel.deleteSelected(questionId, SelectType.QUESTION)
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }
}




