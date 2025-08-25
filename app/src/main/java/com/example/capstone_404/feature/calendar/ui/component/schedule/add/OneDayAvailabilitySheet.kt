package com.example.capstone_404.feature.calendar.ui.component.schedule.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.schedule.add.DayAvailability
import com.example.capstone_404.feature.calendar.model.schedule.add.ScheduleEditorState
import com.example.capstone_404.feature.calendar.model.schedule.add.calcOneDayAvailability
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.Sub2
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneDayAvailabilitySheet(
    editor: ScheduleEditorState,
    selectedMemberIds: Set<Int>,
    schedulesByDate: Map<LocalDate, List<Schedule>>,
    initialMonth: YearMonth,
    zoneId: ZoneId,
    onSelectDate: (LocalDate) -> Unit,
    onOptimize: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selected by remember { mutableStateOf<LocalDate?>(null) }

    val calendarState = rememberCalendarState(
        startMonth = initialMonth,
        endMonth = initialMonth.plusMonths(3),
        firstVisibleMonth = initialMonth,
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )

    val availability = remember(
        editor, selectedMemberIds, schedulesByDate, zoneId, initialMonth
    ) {
        calcOneDayAvailability(
            centerMonth = initialMonth,
            editor = editor,
            selectedMemberIds = selectedMemberIds,
            schedulesByDate = schedulesByDate,
            zoneId = zoneId
        )
    }

    val visibleMonth = calendarState.firstVisibleMonth.yearMonth
    val monthLabel = remember(visibleMonth) {
        DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA).format(visibleMonth)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Stroke) },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                GuideText(Main, "선택 가능")
                GuideText(Sub2, "최적화 가능")
                GuideText(Error, "선택 불가")
            }

            val canPrev = visibleMonth > calendarState.startMonth
            val canNext = visibleMonth < calendarState.endMonth

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이전 달
                IconButton(
                    onClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(visibleMonth.minusMonths(1))
                            selected = null
                        }
                    },
                    enabled = canPrev
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "이전 달",
                        tint = if (canPrev) TextBlack else TextGray
                    )
                }
                // 보여지는 달
                Text(
                    text = monthLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextBlack
                )
                // 다음 달
                IconButton(
                    onClick = {
                        scope.launch {
                            calendarState.animateScrollToMonth(visibleMonth.plusMonths(1))
                            selected = null
                        }
                    },
                    enabled = canNext
                ) {
                    Icon(
                        painterResource(R.drawable.ic_next),
                        contentDescription = "다음 달",
                        tint = if (canNext) TextBlack else TextGray
                    )
                }
            }
            // 캘린더 Column
            Column {
                // 요일 Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1.dp)
                        .border(1.dp, Stroke, RoundedCornerShape(4.dp))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("일", "월", "화", "수", "목", "금", "토").forEachIndexed { index, label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = when (index) {
                                0 -> Color.Red
                                6 -> Color.Blue
                                else -> TextBlack
                            }
                        )
                    }
                }
                Spacer(Modifier.height(1.dp))
                // 날짜 영역
                HorizontalCalendar(
                    state = calendarState,
                    dayContent = { day ->
                        val date = day.date
                        val position = day.position
                        val avail = availability[date]

                        val bg = when (avail) {
                            DayAvailability.SELECTABLE -> Main.copy(alpha = 0.35f)
                            DayAvailability.OPTIMIZATION -> Sub2.copy(alpha = 0.35f)
                            DayAvailability.BLOCKED -> Error.copy(alpha = 0.35f)
                            null -> Error.copy(alpha = 0.35f)
                        }

                        val isSelected = selected == date

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(top = 2.dp, start = 1.dp, end = 1.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Main else Stroke,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(bg, RoundedCornerShape(6.dp))
                                .clickable(enabled = position == DayPosition.MonthDate && avail != null) {
                                    selected = date
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when (date.dayOfWeek) {
                                    DayOfWeek.SUNDAY -> if (position == DayPosition.MonthDate) Color.Red else TextGray
                                    DayOfWeek.SATURDAY -> if (position == DayPosition.MonthDate) Color.Blue else TextGray
                                    else -> if (position == DayPosition.MonthDate) TextBlack else TextGray
                                }
                            )
                        }
                    }
                )
            }
            // 선택된 유형별 출력 결과
            val pickedAvail = selected?.let { availability[it] }
            val (label, enabled) = when (pickedAvail) {
                DayAvailability.SELECTABLE -> "날짜 선택" to true
                DayAvailability.OPTIMIZATION -> "최적화 진행" to true
                DayAvailability.BLOCKED -> "선택 불가" to false
                null -> "원하는 날짜를 선택해 주세요" to false
            }
            ButtonDefault(
                text = label,
                enabled = enabled,
                onClick = {
                    val finalDate = selected ?: return@ButtonDefault
                    when (pickedAvail) {
                        DayAvailability.SELECTABLE  -> { onSelectDate(finalDate); onDismiss() }
                        DayAvailability.OPTIMIZATION -> { onOptimize(finalDate) }
                        else -> Unit
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GuideText(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )
    }
}