package com.example.capstone_404.feature.diary.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.DiaryEntry
import com.example.capstone_404.feature.diary.model.DiaryTab
import com.example.capstone_404.feature.diary.ui.component.DiaryItemCard
import com.example.capstone_404.feature.diary.ui.component.QuestionItemCard
import com.example.capstone_404.ui.theme.TextGray

// 그룹 가입 다이어리 상태
@Composable
fun DiaryJoinedContent(
    selectedTab: DiaryTab,
    diaryEntries: List<DiaryEntry>,
    questions: List<Question>,
    onDiaryClick: (DiaryEntry) -> Unit = {},
    onQuestionClick: (Question) -> Unit = {}
) {
    when (selectedTab) {
        DiaryTab.DIARY -> {
            if (diaryEntries.isEmpty()) {
                EmptyStateContent(
                    message = "작성된 다이어리가 없습니다.",
                )
            } else {
                DiaryList(
                    diaries = diaryEntries,
                    onClick = onDiaryClick
                )
            }
        }
        DiaryTab.QUESTION -> {
            if (questions.isEmpty()) {
                EmptyStateContent(
                    message = "작성된 공통 질문이 없습니다."
                )
            } else {
                QuestionList(
                    questions = questions,
                    onClick = onQuestionClick
                )
            }
        }
    }
}

// 빈 상태
@Composable
fun EmptyStateContent(
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// 공통 질문 리스트
@Composable
fun QuestionList(
    questions: List<Question>,
    onClick: (Question) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 리스트에 데이터가 있을 때 첫 번째 항목(최신)으로 스크롤
    LaunchedEffect(questions) {
        if (questions.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(questions, key = { it.id }) { q ->
            QuestionItemCard(
                question = q,
                onClick = { onClick(q) }
            )
        }
    }
}

// 다이어리 리스트
@Composable
fun DiaryList(
    diaries: List<DiaryEntry>,
    onClick: (DiaryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 리스트에 데이터가 있을 때 첫 번째 항목(최신)으로 스크롤
    LaunchedEffect(diaries) {
        if (diaries.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(diaries, key = { it.id }) { diary ->
            DiaryItemCard(
                diary = diary,
                onClick = { onClick(diary) }
            )
        }
    }
}