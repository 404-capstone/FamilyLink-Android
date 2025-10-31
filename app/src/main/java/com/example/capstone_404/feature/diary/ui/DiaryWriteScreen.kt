package com.example.capstone_404.feature.diary.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.diary.model.WriteStep
import com.example.capstone_404.feature.diary.ui.content.DiaryWriteStepContent
import com.example.capstone_404.feature.diary.ui.content.DiaryWriteQuestionContent
import com.example.capstone_404.feature.diary.ui.content.DiaryWriteLoadingContent
import com.example.capstone_404.feature.diary.ui.content.DiaryWriteResultContent
import com.example.capstone_404.feature.diary.viewmodel.DiaryWriteViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.DetailBg

@Composable
fun DiaryWriteScreen(
    viewModel: DiaryWriteViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    // 오늘의 질문 로드
    LaunchedEffect(Unit) {
        viewModel.loadTodayQuestions(
            onSuccess = { },
            onError = { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
        )
    }

    // 에러 메시지 처리
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // 뒤로가기
    BackHandler {
        when (uiState.step) {
            WriteStep.QUESTION -> viewModel.onPrevToDiary()
            WriteStep.LOADING -> {}
            WriteStep.RESULT -> {
                onNavigateBack()
            }
            WriteStep.DIARY -> onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = if (uiState.step == WriteStep.RESULT) "피드백 결과" else "다이어리 작성",
                navigationType = NavigationType.BACK,
                onNavigationClick = {
                    when (uiState.step) {
                        WriteStep.QUESTION -> viewModel.onPrevToDiary()
                        WriteStep.LOADING -> {}
                        WriteStep.RESULT -> {
                            onNavigateBack()
                        }
                        WriteStep.DIARY -> onNavigateBack()
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .background(DetailBg)
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 16.dp
                maxWidth < 600.dp -> 24.dp
                else -> 40.dp
            }
            
            val verticalPadding = when {
                maxHeight < 600.dp -> 16.dp
                maxHeight < 800.dp -> 24.dp
                else -> 32.dp
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val slidePx = with(LocalDensity.current) { 24.dp.roundToPx() }
                val dur = 200
                AnimatedContent(
                    targetState = uiState.step,
                    transitionSpec = {
                        // 로딩 단계 전/후는 페이드만 사용
                        if (initialState == WriteStep.LOADING || targetState == WriteStep.LOADING) {
                            fadeIn(tween(dur, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(tween(dur, easing = FastOutSlowInEasing))
                        } else if (initialState == WriteStep.DIARY && targetState == WriteStep.QUESTION) {
                            // 다음 단계로 이동
                            (slideInHorizontally(
                                initialOffsetX = { slidePx },
                                animationSpec = tween(dur, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(dur, easing = FastOutSlowInEasing))) togetherWith
                            (slideOutHorizontally(
                                targetOffsetX = { -slidePx },
                                animationSpec = tween(dur, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(dur, easing = FastOutSlowInEasing)))
                        } else if (initialState == WriteStep.QUESTION && targetState == WriteStep.DIARY) {
                            // 이전 단계로 이동
                            (slideInHorizontally(
                                initialOffsetX = { -slidePx },
                                animationSpec = tween(dur, easing = FastOutSlowInEasing)
                            ) + fadeIn(tween(dur, easing = FastOutSlowInEasing))) togetherWith
                            (slideOutHorizontally(
                                targetOffsetX = { slidePx },
                                animationSpec = tween(dur, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(dur, easing = FastOutSlowInEasing)))
                        } else {
                            fadeIn(tween(dur, easing = FastOutSlowInEasing)) togetherWith
                            fadeOut(tween(dur, easing = FastOutSlowInEasing))
                        }
                    },
                    label = "step_transition"
                ) { step ->
                    when (step) {
                        WriteStep.DIARY -> DiaryWriteStepContent(
                            diaryText = uiState.diaryText,
                            helperMessage = viewModel.getHelperMessage(),
                            canProceed = viewModel.canProceedFromDiary(),
                            onDiaryChange = { viewModel.onDiaryChange(it) },
                            onNext = { viewModel.onNextFromDiary() },
                            horizontalPadding = horizontalPadding,
                            verticalPadding = verticalPadding
                        )

                        WriteStep.QUESTION -> DiaryWriteQuestionContent(
                            questionTexts = uiState.questionTexts,
                            answerTexts = uiState.answerTexts,
                            onAnswerChange = { idx, txt -> viewModel.onAnswerChange(idx, txt) },
                            onPrev = { viewModel.onPrevToDiary() },
                            onSubmit = { viewModel.onSubmit() },
                            horizontalPadding = horizontalPadding,
                            verticalPadding = verticalPadding
                        )

                        WriteStep.LOADING -> DiaryWriteLoadingContent()

                        WriteStep.RESULT -> DiaryWriteResultContent(
                            result = requireNotNull(uiState.result),
                            onDone = { onNavigateBack() },
                            horizontalPadding = horizontalPadding,
                            verticalPadding = verticalPadding
                        )
                    }
                }
            }
        }
    }
}