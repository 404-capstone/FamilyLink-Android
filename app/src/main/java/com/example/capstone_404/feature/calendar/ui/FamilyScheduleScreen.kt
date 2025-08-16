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
import com.example.capstone_404.feature.calendar.model.dateFormatter
import com.example.capstone_404.feature.calendar.model.scheduleadd.FamilyMode
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.AllDaySwitch
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.AvailableDateRow
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.SelectModeRow
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.FamilyParticipants
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.OneDayAvailabilitySheet
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.OptimizeResultSheet
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleDateTimeRow
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleInputField
import com.example.capstone_404.feature.calendar.ui.component.scheduleadd.ScheduleOnlyTimeRow
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleDateDialog
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleTimeDialog
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun FamilyScheduleScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val family by viewModel.familyScheduleState.collectAsState()
    val members = remember(userIdToRole) { userIdToRole.toList() }
    val zoneId = remember { ZoneId.systemDefault() }

    val availLabel = remember(family.optSelectedDate) {
        family.optSelectedDate?.format(dateFormatter) ?: "가능한 날짜 확인"
    }

    var target by remember { mutableStateOf(TimeTarget.START) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    val schedulesByDate by viewModel.schedulesByDate.collectAsState()

    var showAvailSheet by remember { mutableStateOf(false) }

    val optimizeResult by viewModel.optimizeResult.collectAsState()
    val isLoading = viewModel.isLoading
    val isSaveLoading = viewModel.isSaveLoading

    if (isLoading) {
        LoadingDialog("일정을 최적화 중이에요\n잠시만 기다려주세요!")
    }

    if (isSaveLoading) {
        LoadingDialog("일정을 저장하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "가족 일정 추가",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = onClose,
                rightButton = {
                    IconButton(
                        onClick = {
                            if (family.selectedMemberIds.isEmpty()) {
                                Toast.makeText(context, "참여자를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            if (family.mode== FamilyMode.ONE_DAY && family.optSelectedDate == null) {
                                Toast.makeText(context, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            val title = family.title.trim()
                            if (title.isEmpty()) {
                                Toast.makeText(context, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }
                            viewModel.addFamilySchedule(
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
                        value = family.title,
                        onValueChange = { viewModel.setFamilyTitle(it) },
                        placeholder = "제목을 입력하세요"
                    )
                    Text(
                        text = "${family.title.length} / 20",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                // 하루|며칠 선택
                SelectModeRow(
                    selected = family.mode,
                    onSelect = { viewModel.setFamilyMode(it) }
                )
                // 종일 스위치
                AllDaySwitch(
                    checked = family.editor.isAllDay,
                    onCheckedChange = { enabled ->
                        viewModel.setFamilyAllDay(enabled, zoneId)
                    }
                )

                if (family.mode == FamilyMode.ONE_DAY) {
                    // 하루 - 시간만
                    ScheduleOnlyTimeRow(
                        startMillis = family.editor.startMillis,
                        endMillis = family.editor.endMillis,
                        allDay = family.editor.isAllDay,
                        onStartClick = {
                            target = TimeTarget.START
                            showTimeDialog = true
                        },
                        onEndClick = {
                            target = TimeTarget.END
                            showTimeDialog = true
                        },
                        icon = painterResource(R.drawable.ic_time)
                    )
                    AvailableDateRow(
                        label = availLabel,
                        onClick = {
                            if (family.selectedMemberIds.isEmpty()) {
                                Toast.makeText(context, "참여자를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                            } else {
                                showAvailSheet = true
                            }
                        }
                    )
                } else {
                    // 며칠 - 날짜&시간
                    ScheduleDateTimeRow(
                        millis = family.editor.startMillis,
                        allDay = family.editor.isAllDay,
                        onDateClick = {
                            target = TimeTarget.START
                            showDateDialog = true
                        },
                        onTimeClick = {
                            target = TimeTarget.START
                            showTimeDialog = true
                        },
                        icon = painterResource(R.drawable.ic_start)
                    )
                    ScheduleDateTimeRow(
                        millis = family.editor.endMillis,
                        allDay = family.editor.isAllDay,
                        onDateClick = {
                            target = TimeTarget.END
                            showDateDialog = true
                        },
                        onTimeClick = {
                            target = TimeTarget.END
                            showTimeDialog = true
                        },
                        icon = painterResource(R.drawable.ic_end)
                    )
                }
                // 장소
                ScheduleInputField(
                    value = family.location,
                    onValueChange = { viewModel.setFamilyLocation(it) },
                    placeholder = "장소 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_location)
                )
                // 메모
                ScheduleInputField(
                    value = family.memo,
                    onValueChange = { viewModel.setFamilyMemo(it) },
                    placeholder = "메모 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_help)
                )
                // 참여자 선택(가족 일정 공용)
                FamilyParticipants(
                    members = members,
                    selectedIds = family.selectedMemberIds,
                    onToggle = { id -> viewModel.toggleFamilyMember(id) },
                    onSetSelectedIds = { next -> viewModel.setFamilyMembers(next) }
                )
            }
        }
        // 날짜 다이얼로그
        if (family.mode == FamilyMode.MULTI_DAYS && showDateDialog) {
            ScheduleDateDialog(
                target = target,
                allDay = family.editor.isAllDay,
                startMillis = family.editor.startMillis,
                endMillis = family.editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showDateDialog = false },
                onConfirm = { newStart, newEnd ->
                    viewModel.setFamilyRange(newStart, newEnd)
                },
                onFamily = true
            )
        }
        // 시간 다이얼로그
        if (showTimeDialog) {
            ScheduleTimeDialog(
                target = target,
                startMillis = family.editor.startMillis,
                endMillis = family.editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showTimeDialog = false },
                onConfirm = { newStart, newEnd ->
                    viewModel.setFamilyRange(newStart, newEnd)
                }
            )
        }
        // 최적화 가능 유무 포함한 캘린더 바텀 시트
        if (showAvailSheet) {
            OneDayAvailabilitySheet(
                editor = family.editor,
                selectedMemberIds = family.selectedMemberIds,
                schedulesByDate = schedulesByDate,
                initialMonth = YearMonth.now(),
                zoneId = zoneId,
                onSelectDate = { pickedDate ->
                    viewModel.setFamilyDateOnly(pickedDate, zoneId)
                },
                onOptimize = { pickedDate ->
                    viewModel.scheduleOptimization(
                        date = pickedDate,
                        zoneId = zoneId,
                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                    )
                },
                onDismiss = { showAvailSheet = false }
            )
        }
        // 최적화 결과 시트
        if (optimizeResult != null) {
            OptimizeResultSheet(
                data = optimizeResult!!,
                userIdToRole = userIdToRole,
                selectedMemberIds = family.selectedMemberIds,
                groupTitle = family.title.ifBlank { "추가할 가족 일정" },
                onCancel = { viewModel.clearOptimizeResult() },
                onApply = { date, result ->
                    viewModel.applyFamilyOptimization(date, result)
                    viewModel.clearOptimizeResult()
                    showAvailSheet = false
                }
            )
        }
    }
}