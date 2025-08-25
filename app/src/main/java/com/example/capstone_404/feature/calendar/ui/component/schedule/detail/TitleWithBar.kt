package com.example.capstone_404.feature.calendar.ui.component.schedule.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.gradientForGroup
import com.example.capstone_404.feature.calendar.model.gradientForPersonal
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun TitleWithBar(
    title: String,
    isGroup: Boolean,
    writerId: Int?,
    participantIds: List<Int>,
    userIdToRole: Map<Int, String>
) {
    // 색상 추출
    val gradientColors = remember(isGroup, writerId, participantIds, userIdToRole) {
        if (isGroup) {
            gradientForGroup(participantIds, userIdToRole)
        } else {
            gradientForPersonal(writerId, userIdToRole)
        }
    }
    val brush = remember(gradientColors) { Brush.linearGradient(gradientColors) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 등록자 색상 세로 바
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(100))
                .background(brush)
        )

        Spacer(Modifier.width(12.dp))
        // 제목
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextBlack,
            maxLines = 2
        )
    }
}