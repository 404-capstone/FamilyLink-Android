package com.example.capstone_404.feature.calendar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.ui.theme.TextBlack
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ScheduleItem(
    schedule: Schedule,
    userIdToRole: Map<Int, String>,
    gradientColors: List<Color>,
    onClick: () -> Unit = {}
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("MM.dd(E) HH:mm", Locale.KOREAN) }
    val timeText = remember(schedule.startTime, schedule.endTime) {
        "${schedule.startTime.format(timeFormatter)} - ${schedule.endTime.format(timeFormatter)}"
    }

    // 등록자 역할 추출
    val roleName = remember(schedule.writerId, userIdToRole) {
        schedule.writerId?.let { userIdToRole[it] }
    }

    // 막대 색상
    val linearBrush = remember(gradientColors) {
        Brush.linearGradient(gradientColors)
    }
    // 원 색상
    val sweepBrush = remember(gradientColors) {
        Brush.sweepGradient(gradientColors)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 본문
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(linearBrush)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextBlack
                )
            }

            // 등록자 색상 + 역할
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(sweepBrush)
                )
                Text(
                    text = if (schedule.isGroup) "가족" else (roleName ?: "알 수 없음"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
            }
        }
    }
}