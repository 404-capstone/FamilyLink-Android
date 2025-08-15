package com.example.capstone_404.feature.calendar.ui.component.scheduleadd

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.core.DayPosition
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateOnlyCalendar(
    calendarState: CalendarState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val scope = rememberCoroutineScope()
    val monthFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREA)
    }

    Column(
        Modifier.background(Color.White)
    ) {
        val visibleYm = calendarState.firstVisibleMonth.yearMonth
        val canPrev = visibleYm > calendarState.startMonth
        val canNext = visibleYm < calendarState.endMonth
        // 년도 & 월 표시 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이전 달
            IconButton(
                onClick = {
                    scope.launch {
                        calendarState.animateScrollToMonth(visibleYm.minusMonths(1))
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
            // 현재 년도/월
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = monthFormatter.format(visibleYm),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextBlack
                )
            }
            // 다음 달
            IconButton(
                onClick = {
                    scope.launch {
                        calendarState.animateScrollToMonth(visibleYm.plusMonths(1))
                    }
                },
                enabled = canNext
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_next),
                    contentDescription = "다음 달",
                    tint = if (canNext) TextBlack else TextGray
                )
            }
        }
        // 요일 Row
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 1.dp)
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
        // 날짜 영역
        HorizontalCalendar(
            state = calendarState,
            dayContent = { day ->
                val date = day.date
                val isToday = date == LocalDate.now()
                val isSelected = date == selectedDate
                val position = day.position
                val isCurrentMonth = position == DayPosition.MonthDate
                val dayOfWeek = date.dayOfWeek

                val dateColor = when {
                    !isCurrentMonth -> TextGray
                    dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                    dayOfWeek == DayOfWeek.SATURDAY -> Color.Blue
                    else -> TextBlack
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
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = if (isToday) {
                            Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Main)
                        } else Modifier
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isToday) Color.White else dateColor
                        )
                    }
                }
            }
        )
    }
}