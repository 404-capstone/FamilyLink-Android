package com.example.capstone_404.feature.calendar.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.TimeTarget
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.AllDaySwitch
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.PersonalParticipant
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleCheckbox
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleDateTimeRow
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleInputField
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleDateDialog
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleTimeDialog
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.Instant
import java.time.ZoneId

@Composable
fun PersonalScheduleScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.personalScheduleState.collectAsState()
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val zoneId = remember { ZoneId.systemDefault() }

    // 시간 변동 체크 박스 활성화 유무
    val canFlex = remember(uiState.editor.isAllDay, uiState.editor.startMillis, uiState.editor.endMillis) {
        val startDate = Instant.ofEpochMilli(uiState.editor.startMillis).atZone(zoneId).toLocalDate()
        val endDate = Instant.ofEpochMilli(uiState.editor.endMillis).atZone(zoneId).toLocalDate()
        !uiState.editor.isAllDay && (startDate == endDate)
    }

    var timeTarget by remember { mutableStateOf(TimeTarget.START) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val isSaveLoading = viewModel.isSaveLoading

    if (isSaveLoading) {
        LoadingDialog("일정을 저장하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "개인 일정 추가",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = onClose,
                rightButton = {
                    IconButton(
                        onClick = {
                            val role = userId?.let { userIdToRole[it] }
                            val finalTitle = uiState.title.ifBlank { "${role ?: "나"}의 일정" }
                            viewModel.addPersonalSchedule(
                                finalTitle = finalTitle,
                                onSuccess = { onSubmit() },
                                onError = { e ->
                                    Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "완료",
                            tint = TextBlack
                        )
                    }
                }
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 입력 필드
                Column {
                    ScheduleInputField(
                        value = uiState.title,
                        onValueChange = { viewModel.setPersonalTitle(it) },
                        placeholder = "제목을 입력하세요"
                    )
                    // 제목 글자 수
                    Text(
                        text = "${uiState.title.length} / 20",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                // 제목 비공개 체크 박스
                ScheduleCheckbox(
                    label = "제목 비공개",
                    checked = uiState.isTitlePrivate,
                    onCheckedChange = { viewModel.setPersonalTitlePrivate(it) },
                    description = "선택 시 그룹원에게 \"{역할명}의 일정\"으로 표시 됩니다"
                )
                // 시간 변동 체크 박스
                ScheduleCheckbox(
                    label = "시간 변동 가능",
                    checked = uiState.isFlexible,
                    onCheckedChange = { viewModel.setPersonalFlexible(it) },
                    description = if (canFlex) "선택 시 일정 최적화가 진행될 때 10시~20시 사이로 변동될 수 있습니다" else "종일 또는 여러 날 일정에는 적용되지 않습니다",
                    enabled = canFlex
                )
                // 종일 스위치
                AllDaySwitch(
                    checked = uiState.editor.isAllDay,
                    onCheckedChange = { enabled ->
                        viewModel.setPersonalAllDay(enabled, ZoneId.systemDefault())
                    }
                )
                // 시작 날짜&시간 Row
                ScheduleDateTimeRow(
                    millis = uiState.editor.startMillis,
                    allDay = uiState.editor.isAllDay,
                    onDateClick = {
                        timeTarget = TimeTarget.START
                        showDateDialog = true
                        },
                    onTimeClick = {
                        timeTarget = TimeTarget.START
                        showTimeDialog = true
                        },
                    icon = painterResource(R.drawable.ic_start)
                )
                // 종료 날짜&시간 Row
                ScheduleDateTimeRow(
                    millis = uiState.editor.endMillis,
                    allDay = uiState.editor.isAllDay,
                    onDateClick = {
                        timeTarget = TimeTarget.END
                        showDateDialog = true
                        },
                    onTimeClick = {
                        timeTarget = TimeTarget.END
                            showTimeDialog = true
                        },
                    icon = painterResource(R.drawable.ic_end)
                )
                // 장소 입력 필드
                ScheduleInputField(
                    value = uiState.location,
                    onValueChange = { viewModel.setPersonalLocation(it) },
                    placeholder = "장소 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_location)
                )
                // 메모 입력 필드
                ScheduleInputField(
                    value = uiState.memo,
                    onValueChange = { viewModel.setPersonalMemo(it) },
                    placeholder = "메모 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_help)
                )
                // 참여자(본인 고정)
                userId?.let {
                    PersonalParticipant(
                        writerUserId = it,
                        userIdToRole = userIdToRole
                    )
                }
            }
        }
        // 날짜 선택 다이얼로그
        if (showDateDialog) {
            ScheduleDateDialog(
                target = timeTarget,
                allDay = uiState.editor.isAllDay,
                startMillis = uiState.editor.startMillis,
                endMillis = uiState.editor.endMillis,
                zoneId = ZoneId.systemDefault(),
                onDismiss = { showDateDialog = false },
                onConfirm = { start, end ->
                    viewModel.setPersonalRange(start, end)
                }
            )
        }
        // 시간 선택 다이얼로그
        if (showTimeDialog) {
            ScheduleTimeDialog(
                target = timeTarget,
                startMillis = uiState.editor.startMillis,
                endMillis = uiState.editor.endMillis,
                zoneId = ZoneId.systemDefault(),
                onDismiss = { showTimeDialog = false },
                onConfirm = { start, end ->
                    viewModel.setPersonalRange(start, end)
                }
            )
        }
    }
}