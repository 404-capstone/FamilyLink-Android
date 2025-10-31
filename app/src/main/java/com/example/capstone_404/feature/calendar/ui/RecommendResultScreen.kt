package com.example.capstone_404.feature.calendar.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.data.retrofit.model.response.RecommendData
import com.example.capstone_404.data.retrofit.model.response.RecommendItem
import com.example.capstone_404.feature.calendar.ui.component.schedule.recommend.RecommendCategoryCard
import com.example.capstone_404.feature.calendar.ui.dialog.RecommendDetailDialog
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.theme.DetailBg
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun RecommendResultScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    data: RecommendData,
    onClose: () -> Unit,
    onAddSchedules: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDetail by remember { mutableStateOf<RecommendItem?>(null) }
    val selected = remember { mutableStateListOf<RecommendItem>() }

    BackHandler(enabled = !showCancelDialog) {
        showCancelDialog = true
    }
    if (showCancelDialog) {
        BackHandler(enabled = true) {
            showCancelDialog = false
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "활동 추천",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = { showCancelDialog = true }
            )
            if (showCancelDialog) {
                ActionDialog(
                    title = "활동 추천을 종료하시겠습니까?",
                    description = "종료 시 활동 추천 결과는 모두 삭제됩니다.",
                    confirmText = "추천 종료",
                    cancelText = "아니오",
                    onConfirm = {
                        showCancelDialog = false
                        viewModel.clearRecommendSelections()
                        onClose()
                    },
                    onDismiss = { showCancelDialog = false }
                )
            }
        },
        bottomBar = {
            BottomActionBar(
                selectedCount = selected.size,
                onSubmit = {
                    viewModel.setRecommendSelections(selected.toList())
                    onAddSchedules()
                }
            )
        }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "활동을 누르면 상세 정보를 볼 수 있어요!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBlack,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                items(data.recommendations) { category ->
                    RecommendCategoryCard(
                        category = category,
                        selected = selected,
                        onToggle = { item ->
                            if (selected.contains(item)) selected.remove(item)
                            else selected.add(item)
                        },
                        onShowDetail = { item ->
                            showDetail = item
                        }
                    )
                }
            }
        }
        // 상세 다이얼로그
        showDetail?.let { item ->
            RecommendDetailDialog(
                item = item,
                onDismiss = { showDetail = null }
            )
        }
    }
}


@Composable
private fun BottomActionBar(
    selectedCount: Int,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 선택 개수
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Stroke),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$selectedCount 개",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack
            )
        }
        // 추가 버튼
        ButtonDefault(
            text = "가족 일정으로 추가",
            enabled = selectedCount > 0,
            onClick = onSubmit
        )
    }
}