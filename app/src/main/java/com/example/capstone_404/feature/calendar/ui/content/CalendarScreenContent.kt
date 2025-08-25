package com.example.capstone_404.feature.calendar.ui.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.ui.component.CalendarBottomSheet
import com.example.capstone_404.feature.calendar.ui.component.CalendarFilter
import com.example.capstone_404.feature.calendar.ui.component.CustomCalendar
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Stroke
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreenContent(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    schedulesByDate: Map<LocalDate, List<Schedule>>,
    userIdToRole: Map<Int, String>,
    onClickedItem: (schedule: Schedule) -> Unit
) {
    // 날짜 출력 범위
    val startMonth = remember { YearMonth.now().minusMonths(6) }
    val endMonth = remember { YearMonth.now().plusMonths(6) }
    // 날짜 필터 리스트
    val monthItems = remember(startMonth, endMonth) {
        generateSequence(startMonth) { it.plusMonths(1) }
            .takeWhile { it <= endMonth }
            .toList()
    }
    // 등록자 필터 리스트
    val roleItems = remember(userIdToRole) {
        listOf("전체") + userIdToRole.values.distinct()
    }
    var selectedRole by rememberSaveable { mutableStateOf("전체") }

    // 캘린더 상태값 설정
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    val scope = rememberCoroutineScope()

    // 필터 드롭다운 상태
    var monthMenuExpanded by remember { mutableStateOf(false) }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    // 현재 표시된 달
    val visibleYm = calendarState.firstVisibleMonth.yearMonth
    // 날짜 필터 출력을 위한 변수
    val ymLabel = "${visibleYm.year}년 ${visibleYm.monthValue}월"

    // 선택된 역할 기준 일정 추출
    val filteredSchedulesByDate = remember(schedulesByDate, selectedRole, userIdToRole) {
        if (selectedRole == "전체") schedulesByDate
        else {
            schedulesByDate.mapValues { (_, list) ->
                list.filter { data ->
                    data.writerId?.let { userId -> userIdToRole[userId] == selectedRole } == true
                }
            }.filterValues { it.isNotEmpty() }
        }
    }

    // 바텀 시트 상태 정의
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    // 각 컨텐츠 높이(픽셀 단위)
    var filterHeightPx by remember { mutableIntStateOf(0) }
    var calendarHeightPx by remember { mutableIntStateOf(0) }
    // 단위 변환용 변수
    val density = LocalDensity.current

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenHpx = with(density) { maxHeight.toPx() }
        val defaultHeight = 100.dp
        val isSuccess = filterHeightPx > 0 && calendarHeightPx > 0

        val peekHeight = if (!isSuccess) {
            defaultHeight
        } else {
            val contentHeight = filterHeightPx + calendarHeightPx
            with(density) { (screenHpx - contentHeight).coerceAtLeast(0f).toDp() }
        }
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = peekHeight,
            sheetSwipeEnabled = true,
            sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Stroke) },
            sheetContainerColor = Background,
            sheetContent = {
                val daySchedules = filteredSchedulesByDate[selectedDate].orEmpty().sortedBy { it.startTime }
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CalendarBottomSheet(
                        selectedDate = selectedDate,
                        schedules = daySchedules,
                        userIdToRole = userIdToRole,
                        onItemClick = { schedule ->
                            onClickedItem(schedule)
                        },
                        onClickToday = {
                            val today = LocalDate.now()
                            onDateSelected(today)
                            scope.launch {
                                calendarState.animateScrollToMonth(YearMonth.from(today))
                                sheetState.partialExpand()
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            // 메인 컨텐츠 Column
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                // 필터 Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .onGloballyPositioned { y -> filterHeightPx = y.size.height },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 날짜 필터
                    CalendarFilter(
                        label = ymLabel,
                        expanded = monthMenuExpanded,
                        onExpandedChange = { monthMenuExpanded = it },
                        items = monthItems,
                        itemLabel = { ym -> "${ym.year}년 ${ym.monthValue}월" },
                        onItemSelected = { ym ->
                            scope.launch { calendarState.animateScrollToMonth(ym) }
                        },
                        selectedItem = visibleYm,
                        theSame = { a, b -> a == b }
                    )
                    // 등록자 필터
                    CalendarFilter(
                        label = selectedRole,
                        expanded = roleMenuExpanded,
                        onExpandedChange = { roleMenuExpanded = it },
                        items = roleItems,
                        itemLabel = { it },
                        onItemSelected = {
                            selectedRole = it
                        },
                        selectedItem = selectedRole
                    )
                }
                // 캘린더
                CustomCalendar(
                    calendarState = calendarState,
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    schedulesByDate = filteredSchedulesByDate,
                    userIdToRole = userIdToRole,
                    contentHeight = { height -> calendarHeightPx = height }
                )
            }
        }
    }
}