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
import com.example.capstone_404.feature.calendar.model.schedule.FormMode
import com.example.capstone_404.feature.calendar.model.schedule.add.FamilyMode
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.AllDaySwitch
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.AvailableDateRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.SelectModeRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.FamilyParticipants
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.OneDayAvailabilitySheet
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.OptimizeResultSheet
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleDateTimeRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleInputField
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleOnlyTimeRow
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
    formMode: String = "add",
    onClose: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (scheduleId: Int) -> Unit
) {
    val context = LocalContext.current
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val members = remember(userIdToRole) { userIdToRole.toList() }
    val zoneId = remember { ZoneId.systemDefault() }

    // 추가용 상태
    val familyAdd by viewModel.familyScheduleState.collectAsState()
    // 수정용 상태
    val edit by viewModel.editState.collectAsState()
    // 추가|수정 구분
    val mode = if (edit != null && formMode == "edit") FormMode.Edit(edit!!.scheduleId) else FormMode.Add

    val availLabel = remember(familyAdd.optSelectedDate) {
        familyAdd.optSelectedDate?.format(dateFormatter) ?: "가능한 날짜 확인"
    }

    var timeTarget by remember { mutableStateOf(TimeTarget.START) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var showAvailSheet by remember { mutableStateOf(false) }
    val schedulesByDate by viewModel.schedulesByDate.collectAsState()
    val optimizeResult by viewModel.optimizeResult.collectAsState()

    val isLoading = viewModel.isLoading
    val isSaveLoading = viewModel.isSaveLoading

    // 진입 상태에 따른 동작 분류
    val isEdit = mode is FormMode.Edit
    // 기본값
    val title = if (isEdit) edit?.title.orEmpty() else familyAdd.title
    val editor = if (isEdit) edit?.editor ?: familyAdd.editor else familyAdd.editor
    val modeValue = if (isEdit) (edit?.familyMode ?: FamilyMode.ONE_DAY) else familyAdd.mode
    val location = if (isEdit) edit?.location.orEmpty() else familyAdd.location
    val memo = if (isEdit) edit?.memo.orEmpty() else familyAdd.memo
    // 입력값 변경
    val setTitle: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditTitle(text) else viewModel.setFamilyTitle(text)
    }
    val setMode: (FamilyMode) -> Unit = { m ->
        if (isEdit) viewModel.setEditMode(m, zoneId) else viewModel.setFamilyMode(m, zoneId)
    }
    val setAllDay: (Boolean) -> Unit = { enabled ->
        if (isEdit) viewModel.setEditAllDay(enabled, zoneId) else viewModel.setFamilyAllDay(enabled, zoneId)
    }
    val setRange: (Long, Long) -> Unit = { s, e ->
        if (isEdit) viewModel.setEditRange(s, e, zoneId) else viewModel.setFamilyRange(s, e, zoneId)
    }
    val setLocation: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditLocation(text) else viewModel.setFamilyLocation(text)
    }
    val setMemo: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditMemo(text) else viewModel.setFamilyMemo(text)
    }

    if (isLoading) {
        LoadingDialog("일정을 최적화 중이에요\n잠시만 기다려주세요!")
    }

    if (isSaveLoading) {
        LoadingDialog("일정을 저장하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = if (isEdit) "가족 일정 수정" else "가족 일정 추가",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = onClose,
                rightButton = {
                    IconButton(
                        onClick = {
                            if (!isEdit) {
                                if (familyAdd.selectedMemberIds.isEmpty()) {
                                    Toast.makeText(context, "참여자를 선택해 주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                    return@IconButton
                                }
                                if (familyAdd.mode == FamilyMode.ONE_DAY && familyAdd.optSelectedDate == null) {
                                    Toast.makeText(context, "날짜를 선택해 주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                    return@IconButton
                                }
                                val finalTitle = familyAdd.title.trim()
                                if (finalTitle.isEmpty()) {
                                    Toast.makeText(context, "제목을 입력해 주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                    return@IconButton
                                }
                                viewModel.addFamilySchedule(
                                    onSuccess = { onAdd() },
                                    onError = { e ->
                                        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                viewModel.submitScheduleEdit(
                                    onSuccess = { onEdit(edit?.scheduleId!!) },
                                    onError = { e ->
                                        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
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
                        value = title,
                        onValueChange = setTitle,
                        placeholder = "제목을 입력하세요"
                    )
                    Text(
                        text = "${title.length} / 20",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                // 하루|며칠 선택
                SelectModeRow(
                    selected = modeValue,
                    onSelect = setMode
                )
                // 종일 스위치
                AllDaySwitch(
                    checked = editor.isAllDay,
                    onCheckedChange = setAllDay
                )

                if (modeValue == FamilyMode.ONE_DAY) {
                    // 하루 - 시간만
                    ScheduleOnlyTimeRow(
                        startMillis = editor.startMillis,
                        endMillis = editor.endMillis,
                        allDay = editor.isAllDay,
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
                    if (!isEdit) {
                        AvailableDateRow(
                            label = availLabel,
                            painterId = R.drawable.ic_calendar,
                            onClick = {
                                if (familyAdd.selectedMemberIds.isEmpty()) {
                                    Toast.makeText(context, "참여자를 선택해 주세요.", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    showAvailSheet = true
                                }
                            }
                        )
                    }
                } else {
                    // 며칠 - 날짜&시간
                    ScheduleDateTimeRow(
                        millis = editor.startMillis,
                        allDay = editor.isAllDay,
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
                    ScheduleDateTimeRow(
                        millis = editor.endMillis,
                        allDay = editor.isAllDay,
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
                }
                // 장소
                ScheduleInputField(
                    value = location,
                    onValueChange = setLocation,
                    placeholder = "장소 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_location)
                )
                // 메모
                ScheduleInputField(
                    value = memo,
                    onValueChange = setMemo,
                    placeholder = "메모 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_help)
                )
                // 참여자 선택(가족 일정 공용)
                if (!isEdit) {
                    FamilyParticipants(
                        members = members,
                        selectedIds = familyAdd.selectedMemberIds,
                        onToggle = { id -> viewModel.toggleFamilyMember(id) },
                        onSetSelectedIds = { next -> viewModel.setFamilyMembers(next) }
                    )
                }
            }
        }
        // 날짜 다이얼로그
        if (modeValue == FamilyMode.MULTI_DAYS && showDateDialog) {
            ScheduleDateDialog(
                target = timeTarget,
                allDay = editor.isAllDay,
                startMillis = editor.startMillis,
                endMillis = editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showDateDialog = false },
                onConfirm = { newStart, newEnd ->
                    setRange(newStart, newEnd)
                },
                onFamily = true
            )
        }
        // 시간 다이얼로그
        if (showTimeDialog) {
            ScheduleTimeDialog(
                target = timeTarget,
                startMillis = editor.startMillis,
                endMillis = editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showTimeDialog = false },
                onConfirm = { newStart, newEnd ->
                    setRange(newStart, newEnd)
                },
                onlyTime = modeValue == FamilyMode.ONE_DAY
            )
        }
        // 최적화 가능 유무 포함한 캘린더 바텀 시트
        if (!isEdit) {
            if (showAvailSheet) {
                OneDayAvailabilitySheet(
                    editor = editor,
                    selectedMemberIds = familyAdd.selectedMemberIds,
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
                    selectedMemberIds = familyAdd.selectedMemberIds,
                    groupTitle = familyAdd.title.ifBlank { "추가할 가족 일정" },
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
}