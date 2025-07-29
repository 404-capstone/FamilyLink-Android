package com.example.capstone_404.feature.group.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.group.model.GuideImageList
import com.example.capstone_404.feature.group.ui.component.OnboardingGuide
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.component.GuideWarningCard

// 설문 안내 페이지
@Composable
fun SurveyIntroScreen(
onNextPage: () -> Unit
) {
    Scaffold(
        topBar = { CustomTopBar(title = "설문 안내") }
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "그룹 가입 완료",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "그림을 오른쪽으로 넘겨 설명을 읽어주세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }

                OnboardingGuide(GuideImageList.surveyGuide)

                GuideWarningCard("설문 결과는 본인만 열람이 가능하며, 다른 사용자에게 공유되지 않습니다!")

                ButtonDefault(
                    text = "설문 진행하기",
                    onClick = { onNextPage() }
                )
            }
        }
    }
}
