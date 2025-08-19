package com.example.capstone_404.feature.calendar.ui.component.schedule.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.gradientForPersonal
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun ParticipantsSection(
    isGroup: Boolean,
    participantIds: List<Int>,
    writerId: Int?,
    userId: Int?,
    iAmJoined: Boolean,
    userIdToRole: Map<Int, String>,
    onToggleJoin: (join: Boolean) -> Unit
) {
    val iconRes = if (isGroup) R.drawable.ic_group_schedule else R.drawable.ic_personal_schedule

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = TextBlack
            )

            Spacer(Modifier.width(12.dp))

            Text(
                text = "참여자",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack
            )

            Spacer(Modifier.weight(1f))
            // 개인 일정 - 참여자 | 가족 일정 - 참여 버튼
            if (isGroup && userId != null) {
                JoinButton(
                    joined = iAmJoined,
                    onClick = { onToggleJoin(!iAmJoined) }
                )
            } else if (!isGroup) {
                val roleLabel = userIdToRole[writerId] ?: "알 수 없음"
                val colors = gradientForPersonal(writerId, userIdToRole)
                RoleChip(
                    label = roleLabel,
                    colors = colors
                )
            }
        }
        // 가족 일정일 때 참여자
        if (isGroup) {
            // 참여자 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(8.dp)
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                participantIds.forEach { userId ->
                    val label = userIdToRole[userId] ?: "알 수 없음"
                    val colors = gradientForPersonal(userId, userIdToRole)
                    RoleChip(
                        label = label,
                        colors = colors
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
private fun RoleChip(
    label: String,
    colors: List<Color>
) {
    val brush = remember(colors) { Brush.sweepGradient(colors) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 역할 색
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Spacer(Modifier.width(8.dp))
        // 역할 명
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )
    }
}

@Composable
private fun JoinButton(
    joined: Boolean,
    onClick: () -> Unit
) {
    val bg = if (joined) Main else ButtonDisabled
    Surface(
        onClick = onClick,
        color = bg,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "참여",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            if (joined) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}