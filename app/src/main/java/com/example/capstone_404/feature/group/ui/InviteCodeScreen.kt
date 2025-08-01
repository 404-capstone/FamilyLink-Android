package com.example.capstone_404.feature.group.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.group.model.InviteCodeStatus
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.component.NavigationType
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun InviteCodeScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onGenerateClick: () -> Unit,
    onRegenerateClick: () -> Unit,
    onShareClick: () -> Unit,
    onClickToBack: () -> Unit
) {
    val status by viewModel.inviteCodeStatus.collectAsState()
    val code by viewModel.inviteCodeValue.collectAsState()

    // 진입 시 코드 조회
    LaunchedEffect(Unit) {
        viewModel.fetchInviteCode()
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "그룹원 초대",
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 텍스트 & 코드 Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "초대 코드",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "초대 코드를 공유하여 그룹원을 초대해 보세요",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    // 코드 출력 Card
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .width(250.dp)
                            .height(58.dp)
                            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            when (status) {
                                InviteCodeStatus.NOT_GENERATED -> {
                                    ButtonOutline(
                                        text = "초대 코드 생성",
                                        style = MaterialTheme.typography.headlineLarge,
                                        onClick = { },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                InviteCodeStatus.GENERATED -> {
                                    Text(
                                        text = code ?: "오류 발생",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = TextBlack,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                InviteCodeStatus.EXPIRED -> {
                                    Text(
                                        text = "만료된 초대 코드",
                                        style = MaterialTheme.typography.headlineLarge,
                                        color = Error,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                InviteCodeStatus.LOADING -> {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // 안내 문구
                    GuideWarningCard("초대 코드의 유효 시간은 10분입니다!\n코드 만료 시 재발급을 진행해 주세요.")
                }

                // 하단 버튼들
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ButtonDefault(
                        text = "코드 재발급",
                        onClick = {  },
                        enabled = (status == InviteCodeStatus.EXPIRED)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ButtonOutline(
                        text = "SNS로 공유",
                        onClick = {  },
                        enabled = (status == InviteCodeStatus.GENERATED)
                    )
                }
            }
        }
    }
}