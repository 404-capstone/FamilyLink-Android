package com.example.capstone_404.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.schedule.recommend.Area
import com.example.capstone_404.feature.calendar.model.schedule.recommend.Areas
import com.example.capstone_404.feature.calendar.ui.component.schedule.recommend.AreaColumn
import com.example.capstone_404.feature.calendar.viewmodel.AreaSelectionViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun AreaSelectionScreen(
    viewModel: AreaSelectionViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onConfirm: (fullName: String) -> Unit
) {
    val siDo by viewModel.selectedSiDo.collectAsState()
    val gu by viewModel.selectedGu.collectAsState()
    val enabled = siDo != null && gu != null

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "활동 추천",
                navigationType = NavigationType.BACK,
                onNavigationClick = onBack
            )
        },
        bottomBar = {
            BottomButtonBar(
                enabled = enabled,
                label = bottomLabel(siDo, gu),
                onReset = {
                    viewModel.resetSelection()
                },
                onConfirm = {
                    val fullName = viewModel.getFullAreaName()
                    fullName?.let { onConfirm(it) }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // 안내 문구 Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Background)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = "원하는 지역을 선택해 주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
                Text(
                    text = "(단일 선택)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
            }
            // 지역 선택지 Row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, Stroke)
            ) {
                // 시·도 리스트
                AreaColumn(
                    title = "시 · 도",
                    items = Areas.siDo,
                    selected = siDo,
                    onSelect = { selectSiDo ->
                        viewModel.setSiDo(selectSiDo)
                    },
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                )

                Spacer(Modifier.width(2.dp))
                // 군·구 리스트
                val guList = remember(siDo) { siDo?.let { viewModel.getCurrentGuList() } ?: emptyList() }
                AreaColumn(
                    title = "군 · 구",
                    items = guList,
                    selected = gu,
                    onSelect = { selectGu -> viewModel.setGu(selectGu) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        }
    }
}

// 초기화+선택 버튼 바텀바
@Composable
private fun BottomButtonBar(
    enabled: Boolean,
    label: String,
    onReset: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 초기화 버튼
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
                .clickable { onReset() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_refresh),
                contentDescription = "초기화",
                tint = Color.White
            )
        }
        // 선택 버튼
        ButtonDefault(
            text = label,
            enabled = enabled,
            onClick = { onConfirm() }
        )
    }
}

// 선택 상태 출력용
private fun bottomLabel(siDo: Area?, gu: Area?): String {
    if (siDo == null || gu == null) return "지역 선택"
    return if (gu.full == "전체") "${siDo.short} 전체 선택" else "${siDo.short} ${gu.short} 선택"
}