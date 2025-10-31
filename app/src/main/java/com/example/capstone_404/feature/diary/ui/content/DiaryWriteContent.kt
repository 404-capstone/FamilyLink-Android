package com.example.capstone_404.feature.diary.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.capstone_404.feature.diary.model.FeedbackResult
import com.example.capstone_404.feature.diary.ui.component.DiaryWriteHelperCard
import com.example.capstone_404.feature.diary.ui.component.ResultSectionCard
import com.example.capstone_404.feature.diary.ui.component.DiaryContentSection
import com.example.capstone_404.feature.diary.ui.component.EmotionAnalysisSection
import com.example.capstone_404.feature.diary.ui.component.AiFeedbackSection
import com.example.capstone_404.feature.diary.ui.component.WriteStepDots
import com.example.capstone_404.feature.diary.ui.component.WriteTitle
import com.example.capstone_404.ui.component.AiMascot
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.theme.GradientBg
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

// 다이어리 작성 단계
@Composable
fun DiaryWriteStepContent(
    diaryText: String,
    helperMessage: String?,
    canProceed: Boolean,
    onDiaryChange: (String) -> Unit,
    onNext: () -> Unit,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        WriteTitle(text = "오늘 하루의 일기를 작성해주세요!")
        // 도움말 카드
        if (helperMessage != null) {
            DiaryWriteHelperCard(message = helperMessage)
            Spacer(modifier = Modifier.height(24.dp))
        }

        OutlinedTextField(
            value = diaryText,
            onValueChange = onDiaryChange,
            placeholder = {
                Text(
                    text = "오늘의 일기…",
                    color = TextGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
            maxLines = 10,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Main,
                unfocusedBorderColor = Main
            ),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Step
        WriteStepDots(currentStep = 1, totalSteps = 2)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            enabled = canProceed,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Main,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "다음",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// 공통 질문 작성 단계
@Composable
fun DiaryWriteQuestionContent(
    questionTexts: List<String>,
    answerTexts: List<String>,
    onAnswerChange: (Int, String) -> Unit,
    onPrev: () -> Unit,
    onSubmit: () -> Unit,
    horizontalPadding: Dp = 24.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .imePadding()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        WriteTitle(text = "질문에 대한 본인의 생각을 작성해주세요!")
        // 도움말 카드
        DiaryWriteHelperCard(message = "그룹원 모두에게 동일한 질문이 제공되며, 해당 내용은 그룹원도 확인할 수 있습니다.")
        Spacer(modifier = Modifier.height(16.dp))

        // 공통 질문 3게
        questionTexts.forEachIndexed { index, q ->
            Text(
                text = q,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = answerTexts.getOrElse(index) { "" },
                onValueChange = { onAnswerChange(index, it) },
                placeholder = { Text(text = "나의 생각은…", color = TextGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Main,
                    unfocusedBorderColor = Main
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 단계
        WriteStepDots(currentStep = 2, totalSteps = 2)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onPrev,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Main
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Main),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "이전",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Main
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "제출 및 피드백 받기",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// 로딩 단계
@Composable
fun DiaryWriteLoadingContent(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(GradientBg)
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
            // AI 캐릭터
            AiMascot()
            // 진행 문구
            Text(
                text = "감정 분석 결과와 피드백을 작성하고 있어요…",
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 안내 문구 추가
            GuideWarningCard("뒤로가기 또는 앱 종료 시 분석이 취소됩니다.")
        }
    }
}

// 결과 단계
@Composable
fun DiaryWriteResultContent(
    result: FeedbackResult,
    onDone: () -> Unit,
    horizontalPadding: Dp = 16.dp,
    verticalPadding: Dp = 16.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .verticalScroll(rememberScrollState())
    ) {
        ResultSectionCard(
            title = "다이어리 내용",
            content = {
                DiaryContentSection(text = result.diaryText)
            }
        )

        ResultSectionCard(
            title = "감정 분석",
            content = {
                EmotionAnalysisSection(emotions = result.emotionResults)
            }
        )

        ResultSectionCard(
            title = "AI 피드백",
            content = {
                AiFeedbackSection(feedback = result.aiFeedback)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Main),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "완료",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
