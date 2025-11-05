package com.example.capstone_404.feature.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.TimeTarget
import com.example.capstone_404.feature.calendar.model.schedule.recommend.ActivityRecommendUiState
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.AvailableDateRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.FamilyParticipants
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleOnlyTimeRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.recommend.InOutDoorRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.recommend.TypeGroupSection
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleTimeDialog
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.theme.DetailBg
import com.example.capstone_404.ui.theme.TextBlack
import java.time.ZoneId

@Composable
fun ActivityRecommendScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onClickArea: () -> Unit,
    onRecommend: (ActivityRecommendUiState) -> Unit
) {
    val uiState by viewModel.activityRecommendState.collectAsState()
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val members = remember(userIdToRole) { userIdToRole.toList() }
    val zoneId = remember { ZoneId.systemDefault() }

    var timeTarget by remember { mutableStateOf(TimeTarget.START) }
    var showTimeDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "활동 추천",
                navigationType = NavigationType.BACK,
                onNavigationClick = onBack
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // 지역
                SectionTitle("1. 원하는 지역을 선택해 주세요")
                AvailableDateRow(
                    label = uiState.area ?: "지역",
                    painterId = R.drawable.ic_location,
                    onClick = onClickArea
                )
                Spacer(Modifier.height(16.dp))
                // 시간대
                SectionTitle("2. 활동을 진행할 시간대를 설정해 주세요")
                ScheduleOnlyTimeRow(
                    startMillis = uiState.editor.startMillis,
                    endMillis = uiState.editor.endMillis,
                    allDay = false,
                    onStartClick = {
                        timeTarget = TimeTarget.START
                        showTimeDialog = true
                    },
                    onEndClick = {
                        timeTarget = TimeTarget.END
                        showTimeDialog = true
                    },
                    icon = painterResource(R.drawable.ic_time)
                )
                Spacer(Modifier.height(16.dp))
                // 참여자
                SectionTitle("3. 활동에 참여할 구성원을 선택해 주세요")
                FamilyParticipants(
                    members = members,
                    selectedIds = uiState.memberIds,
                    onToggle = { id -> viewModel.toggleRecommendMember(id) },
                    onSetSelectedIds = { ids -> viewModel.setRecommendMembers(ids) }
                )
                Spacer(Modifier.height(16.dp))
                // 실내|실외
                SectionTitle("4. 원하는 활동 공간을 선택해 주세요")
                InOutDoorRow(
                    selected = uiState.inOutDoor,
                    onChange = { io -> viewModel.setRecommendInOutDoor(io) }
                )
                Spacer(Modifier.height(16.dp))
                // 활동 성격(최대 3개)
                SectionTitle("5. 원하는 활동 성격을 선택해 주세요")
                TypeGroupSection(
                    typeGroups = uiState.typeGroups,
                    onSelect = { idx, trait -> viewModel.selectTypeInGroup(idx, trait) },
                    onRemoveGroup = { idx -> viewModel.removeTypeGroup(idx) },
                    canAddGroup = uiState.canAddTypeGroup,
                    onAddGroup = { viewModel.addTypeGroup() }
                )
                Spacer(Modifier.height(24.dp))
                // 최종 진행 버튼
                ButtonDefault(
                    text = "활동 추천 진행",
                    enabled = uiState.isValid,
                    onClick = {
                        onRecommend(uiState)
                    }
                )
            }
        }
    }

    // 시간 다이얼로그
    if (showTimeDialog) {
        ScheduleTimeDialog(
            target = timeTarget,
            startMillis = uiState.editor.startMillis,
            endMillis = uiState.editor.endMillis,
            zoneId = zoneId,
            onDismiss = { showTimeDialog = false },
            onConfirm = { start, end ->
                viewModel.setRecommendTimeRange(start, end)
                showTimeDialog = false
            },
            onlyTime = true
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        color = TextBlack,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}