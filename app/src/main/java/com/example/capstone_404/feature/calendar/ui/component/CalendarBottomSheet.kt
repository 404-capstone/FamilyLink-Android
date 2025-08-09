package com.example.capstone_404.feature.calendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.getGradientColors
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarBottomSheet(
    selectedDate: LocalDate,
    schedules: List<Schedule>,
    userIdToRole: Map<Int, String>,
    onItemClick: (Schedule) -> Unit,
    onClickToday: () -> Unit
) {
    val isToday = selectedDate == LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("MM.dd (E)", Locale.KOREAN)),
                style = MaterialTheme.typography.headlineMedium,
                color = TextBlack
            )

            ButtonDefault(
                text = if (isToday) "오늘" else "오늘로 이동",
                onClick = onClickToday,
                enabled = !isToday,
                modifier = Modifier.width(120.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        // 선택된 날짜의 일정 리스트 출력
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    "등록된 일정이 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                schedules.forEach { schedule ->
                    val gradientColors = getGradientColors(schedule, userIdToRole)
                    ScheduleItem(
                        schedule = schedule,
                        userIdToRole = userIdToRole,
                        gradientColors = gradientColors,
                        onClick = { onItemClick(schedule) }
                    )
                }
            }
        }
    }
}