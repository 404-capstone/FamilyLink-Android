package com.example.capstone_404.feature.calendar.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
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
import com.example.capstone_404.feature.calendar.model.schedule.FormMode
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.AllDaySwitch
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.PersonalParticipant
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleCheckbox
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleDateTimeRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ScheduleInputField
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleDateDialog
import com.example.capstone_404.feature.calendar.ui.dialog.ScheduleTimeDialog
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.DetailBg
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.Instant
import java.time.ZoneId

@Composable
fun PersonalScheduleScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    formMode: String = "add",
    onClose: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (scheduleId: Int, writerId: Int) -> Unit
) {
    val context = LocalContext.current
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val zoneId = remember { ZoneId.systemDefault() }

    // 추가용 상태
    val personalAdd by viewModel.personalScheduleState.collectAsState()
    // 수정용 상태
    val edit by viewModel.editState.collectAsState()
    // 추가|수정 구분
    val mode = if (edit != null && formMode == "edit") FormMode.Edit(edit!!.scheduleId) else FormMode.Add

    var timeTarget by remember { mutableStateOf(TimeTarget.START) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val isSaveLoading = viewModel.isSaveLoading

    // 진입 상태에 따른 동작 분류
    val isEdit = mode is FormMode.Edit
    // 기본값
    val title = if (isEdit) edit?.title.orEmpty() else personalAdd.title
    val isFlexible = if (isEdit) (edit?.isFlexible ?: false) else personalAdd.isFlexible
    val editor = if (isEdit) (edit?.editor ?: personalAdd.editor) else personalAdd.editor
    val location = if (isEdit) edit?.location.orEmpty() else personalAdd.location
    val memo = if (isEdit) edit?.memo.orEmpty() else personalAdd.memo
    // 입력값 변경
    val setTitle: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditTitle(text) else viewModel.setPersonalTitle(text)
    }
    val setFlexible: (Boolean) -> Unit = { checked ->
        if (isEdit) viewModel.setEditFlexible(checked) else viewModel.setPersonalFlexible(checked)
    }
    val setAllDay: (Boolean) -> Unit = { enabled ->
        if (isEdit) viewModel.setEditAllDay(enabled, zoneId) else viewModel.setPersonalAllDay(enabled, zoneId)
    }
    val setRange: (Long, Long) -> Unit = { s, e ->
        if (isEdit) viewModel.setEditRange(s, e, zoneId) else viewModel.setPersonalRange(s, e, zoneId)
    }
    val setLocation: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditLocation(text) else viewModel.setPersonalLocation(text)
    }
    val setMemo: (String) -> Unit = { text ->
        if (isEdit) viewModel.setEditMemo(text) else viewModel.setPersonalMemo(text)
    }

    // 시간 변동 체크 박스 활성화 유무
    val canFlex = remember(editor.isAllDay, editor.startMillis, editor.endMillis) {
        val startDate = Instant.ofEpochMilli(editor.startMillis).atZone(zoneId).toLocalDate()
        val endDate   = Instant.ofEpochMilli(editor.endMillis).atZone(zoneId).toLocalDate()
        !editor.isAllDay && (startDate == endDate)
    }

    if (isSaveLoading) {
        LoadingDialog("일정을 저장하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = if (isEdit) "개인 일정 수정" else "개인 일정 추가",
                navigationType = NavigationType.CLOSE,
                onNavigationClick = onClose,
                rightButton = {
                    IconButton(
                        onClick = {
                            if (!isEdit) {
                                val role = userId?.let { userIdToRole[it] }
                                val finalTitle = personalAdd.title.ifBlank { "${role ?: "나"}의 일정" }
                                viewModel.addPersonalSchedule(
                                    finalTitle = finalTitle,
                                    onSuccess = { onAdd() },
                                    onError = { e ->
                                        Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                viewModel.submitScheduleEdit(
                                    onSuccess = { onEdit(edit?.scheduleId!!, userId!!) },
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
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 입력 필드
                Column {
                    ScheduleInputField(
                        value = title,
                        onValueChange = setTitle,
                        placeholder = "제목을 입력하세요"
                    )
                    // 제목 글자 수
                    Text(
                        text = "${title.length} / 20",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                // 제목 비공개 체크 박스
                if (!isEdit) {
                    ScheduleCheckbox(
                        label = "제목 비공개",
                        checked = personalAdd.isTitlePrivate,
                        onCheckedChange = { viewModel.setPersonalTitlePrivate(it) },
                        description = "선택 시 그룹원에게 \"{역할명}의 일정\"으로 표시 됩니다"
                    )
                }
                // 시간 변동 체크 박스
                ScheduleCheckbox(
                    label = "시간 변동 가능",
                    checked = isFlexible && canFlex,
                    onCheckedChange = setFlexible,
                    description = if (canFlex) "선택 시 일정 최적화가 진행될 때 10시~20시 사이로 변동될 수 있습니다" else "종일 또는 여러 날 일정에는 적용되지 않습니다",
                    enabled = canFlex
                )
                // 종일 스위치
                AllDaySwitch(
                    checked = editor.isAllDay,
                    onCheckedChange = setAllDay
                )
                // 시작 날짜&시간 Row
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
                // 종료 날짜&시간 Row
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
                // 장소 입력 필드
                ScheduleInputField(
                    value = location,
                    onValueChange = setLocation,
                    placeholder = "장소 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_location)
                )
                // 메모 입력 필드
                ScheduleInputField(
                    value = memo,
                    onValueChange = setMemo,
                    placeholder = "메모 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_help)
                )
                // 참여자(본인 고정)
                if (!isEdit) {
                    userId?.let {
                        PersonalParticipant(
                            writerUserId = it,
                            userIdToRole = userIdToRole
                        )
                    }
                }
            }
        }
        // 날짜 선택 다이얼로그
        if (showDateDialog) {
            ScheduleDateDialog(
                target = timeTarget,
                allDay = editor.isAllDay,
                startMillis = editor.startMillis,
                endMillis = editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showDateDialog = false },
                onConfirm = { start, end ->
                    setRange(start, end)
                }
            )
        }
        // 시간 선택 다이얼로그
        if (showTimeDialog) {
            ScheduleTimeDialog(
                target = timeTarget,
                startMillis = editor.startMillis,
                endMillis = editor.endMillis,
                zoneId = zoneId,
                onDismiss = { showTimeDialog = false },
                onConfirm = { start, end ->
                    setRange(start, end)
                }
            )
        }
    }
}