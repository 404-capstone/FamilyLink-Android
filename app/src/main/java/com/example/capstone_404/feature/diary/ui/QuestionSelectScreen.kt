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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.capstone_404.feature.diary.model.DiarySelectUiState
import com.example.capstone_404.feature.diary.model.QuestionDetail
import com.example.capstone_404.feature.diary.ui.component.AccordionQuestionCard
import com.example.capstone_404.feature.diary.viewmodel.DiarySelectViewModel
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

    // 각 질문의 확장/축소 상태를 관리
    val expandedQuestions = remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(questionId) {
        viewModel.selectQuestion(questionId)
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "공통질문 상세",
                navigationType = NavigationType.BACK,
                onNavigationClick = onNavigateBack
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

                                AccordionQuestionCard(
                                    index = idx,
                                    authorChips = item.question.responders,
                                    text = item.question.question,
                                    answers = item.answers,
                                    isExpanded = expandedQuestions.value.contains(idx),
                                    onToggle = {
                                        expandedQuestions.value = if (expandedQuestions.value.contains(idx)) {
                                            expandedQuestions.value - idx  // 접기
                                        } else {
                                            expandedQuestions.value + idx  // 펼치기
                                        }
                                    }
                                )
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
                // 삭제 기능 없음
                is DiarySelectUiState.Deleted -> { }
            }
        }
    }
}





