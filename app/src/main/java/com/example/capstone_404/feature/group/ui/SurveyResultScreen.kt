package com.example.capstone_404.feature.group.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.group.model.getSurveyDescription
import com.example.capstone_404.feature.group.ui.component.ResultBox
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.DetailBg
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun SurveyResultScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onClickGoHome: () -> Unit
) {
    val surveyResult by viewModel.surveyResultFlow.collectAsState(initial = null)
    val description = getSurveyDescription(surveyResult?.level ?: "")
    val userName by viewModel.nicknameFlow.collectAsState(initial = null)

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "설문 결과",
                navigationType = NavigationType.BACK,
                onNavigationClick = onClickGoHome
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DetailBg)
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
                // 안내 텍스트
                Column {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = AbsoluteAlignment.Left
                    ) {
                        Text(
                            text = "${userName}님의",
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "가족의사소통 분석 결과입니다.",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    // 결과 Card
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Stroke,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ResultBox(
                                    title = "수준",
                                    value = surveyResult?.level ?: "기록 없음",
                                    modifier = Modifier.weight(1f)
                                )
                                ResultBox(
                                    title = "원점수",
                                    value = "${surveyResult?.score ?: 0}점",
                                    modifier = Modifier.weight(1f)
                                )
                                ResultBox(
                                    title = "퍼센트(%)",
                                    value = "${surveyResult?.percent ?: 0}%",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    // 경계선
                    HorizontalDivider(
                        modifier = Modifier
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(100))
                            .clip(RoundedCornerShape(100)),
                        thickness = 6.dp,
                        color = Main
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    // 추가 설명 Card
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = Stroke,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "추가 설명",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextBlack
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${userName}님은 $description",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBlack
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // 안내 문구
                    GuideWarningCard("해당 결과는 ‘FACES IV의 가족의사소통 척도(FCS)’를 기반으로 분석된 것이며 참고용입니다.")
                }
                ButtonDefault(
                    text = "홈으로 이동",
                    onClick = onClickGoHome
                )
            }
        }
    }
}