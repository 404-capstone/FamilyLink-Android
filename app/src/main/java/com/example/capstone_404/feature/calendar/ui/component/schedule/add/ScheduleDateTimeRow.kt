package com.example.capstone_404.feature.calendar.ui.component.schedule.add

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.dateFormatter
import com.example.capstone_404.feature.calendar.model.timeFormatter
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.Instant
import java.time.ZoneId

// 날짜&시간 선택 Row
@Composable
fun ScheduleDateTimeRow(
    millis: Long,
    allDay: Boolean,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    icon: Painter,
    zoneId: ZoneId = ZoneId.systemDefault()
) {
    val dateTime = remember(millis, zoneId) {
        Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Icon(painter = icon, contentDescription = "시작/종료", tint = TextBlack)


        // 날짜 버튼
        DateTimeButton(
            text = dateFormatter.format(dateTime.toLocalDate()),
            onClick = onDateClick,
            modifier = Modifier.weight(1f)
        )

        // 시간 버튼
        DateTimeButton(
            text = if (allDay) "종일" else timeFormatter.format(dateTime.toLocalTime()),
            onClick = onTimeClick,
            enabled = !allDay,
            modifier = Modifier.weight(1f)
        )
    }
}

// 시간만 있는 Row
@Composable
fun ScheduleOnlyTimeRow(
    startMillis: Long,
    endMillis: Long,
    allDay: Boolean,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter,
    zoneId: ZoneId = ZoneId.systemDefault()
) {
    val startInit = remember(startMillis, zoneId) {
        Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDateTime()
    }
    val endInit = remember(endMillis, zoneId) {
        Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDateTime()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {

        Icon(painter = icon, contentDescription = "시간", tint = TextBlack)


        // 시작 시간 버튼
        DateTimeButton(
            text = if (allDay) "종일" else timeFormatter.format(startInit.toLocalTime()),
            onClick = onStartClick,
            enabled = !allDay,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "~",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBlack
        )
        // 종료 시간 버튼
        DateTimeButton(
            text = if (allDay) "종일" else timeFormatter.format(endInit.toLocalTime()),
            onClick = onEndClick,
            enabled = !allDay,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DateTimeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) TextBlack else TextGray
        )
    }
}