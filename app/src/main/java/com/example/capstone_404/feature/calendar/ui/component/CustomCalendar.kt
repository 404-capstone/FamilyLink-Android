package com.example.capstone_404.feature.calendar.ui.component

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.getGradientColors
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CustomCalendar(
    calendarState: CalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    schedulesByDate: Map<LocalDate, List<Schedule>>,
    userIdToRole: Map<Int, String>,
    contentHeight: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .background(Color.White)
            .onGloballyPositioned { y -> contentHeight(y.size.height) }
    ) {
        // 요일 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Stroke),
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
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .weight(1f)
                )
            }
        }

        // 캘린더 날짜 영역
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                val date = day.date
                val isToday = date == LocalDate.now()
                val isSelected = date == selectedDate
                val position = day.position
                val isCurrentMonth = position == DayPosition.MonthDate
                val dayOfWeek = date.dayOfWeek
                val schedules = schedulesByDate[date] ?: emptyList()

                val dateColor = when {
                    !isCurrentMonth -> TextGray
                    dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                    dayOfWeek == DayOfWeek.SATURDAY -> Color.Blue
                    else -> TextBlack
                }

                val lineBrushes: List<Brush> = remember(schedules, userIdToRole) {
                    schedules.map { schedule ->
                        if (schedule.isGroup) {
                            val colors = getGradientColors(schedule, userIdToRole)
                            Brush.linearGradient(colors)
                        } else {
                            val roleName = schedule.writerId?.let { userIdToRole[it] }
                            val color = roleName?.let {
                                val splitRole = parseRoleAndOrder(it)
                                splitRole.first.getColor(splitRole.second)
                            } ?: Error
                            SolidColor(color)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(top = 2.dp, start = 1.dp, end = 1.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Main else Stroke
                        )
                        .clickable {
                            if (!isCurrentMonth) {
                                scope.launch {
                                    calendarState.animateScrollToMonth(YearMonth.from(date))
                                }
                            }
                            onDateSelected(date)
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if(isToday) Main else Color.Transparent)
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isToday) Color.White else dateColor
                            )
                        }
                        // 일정 있을 시 등록자 색상 줄로 표시
                        if (lineBrushes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                lineBrushes.forEach { brush ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .background(brush)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}