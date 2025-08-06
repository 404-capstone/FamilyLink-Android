package com.example.capstone_404.feature.group.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.group.model.surveyQuestions
import com.example.capstone_404.feature.group.ui.component.SurveyItem
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun SurveyListScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onSubmitComplete: () -> Unit,
    onClickToBack: () -> Unit
) {
    // 설문 응답 저장
    val surveyResponses = viewModel.surveyResponses
    // 로딩 여부
    val isLoading = viewModel.isLoading
    val isSaved by viewModel.isSaved.collectAsState()

    // 설문 결과 저장 완료 시 이동
    LaunchedEffect(isSaved) {
        if (isSaved) {
            onSubmitComplete()
        }
    }
    if (isLoading) {
        LoadingDialog("점수를 계산하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "설문지",
                navigationType = NavigationType.BACK,
                onNavigationClick = onClickToBack
            )
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
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "본인의 가족에 대해 가장 잘\n설명해주는 항목을 선택해 주세요",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(24.dp))
                // 설문지 리스트
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(surveyQuestions) { question ->
                        SurveyItem(
                            questionNumber = question.id,
                            questionText = question.question,
                            selectedAnswer = surveyResponses[question.id],
                            onAnswerSelected = { selected ->
                                viewModel.onAnswerSelected(question.id, selected)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ButtonDefault(
                    text = "설문 제출",
                    onClick = { viewModel.submitSurvey() },
                    enabled = viewModel.isSurveySubmitEnabled(surveyQuestions.size)
                )
            }
        }
    }
}