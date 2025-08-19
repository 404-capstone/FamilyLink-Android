package com.example.capstone_404.feature.calendar.ui.component.schedule.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.colorForUserId
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.example.capstone_404.ui.theme.TextWhite
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

// 개인 일정 참여자(본인)
@Composable
fun PersonalParticipant(
    writerUserId: Int,
    userIdToRole: Map<Int, String>,
    modifier: Modifier = Modifier
) {
    val roleName = userIdToRole[writerUserId] ?: "나"
    val roleColor = remember(writerUserId, userIdToRole) {
        colorForUserId(writerUserId, userIdToRole)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(horizontal = 12.dp)
    ) {
        Icon(
            painterResource(R.drawable.ic_personal_schedule),
            contentDescription = null,
            tint = TextGray
        )
        Spacer(Modifier.width(16.dp))

        Text(
            "참여자",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(roleColor)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = roleName,
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray,
                maxLines = 1
            )
        }
    }
}

// 가족 일정 참여자(그룹원 선택)
@Composable
fun FamilyParticipants(
    members: List<Pair<Int, String>>,
    selectedIds: Set<Int>,
    onToggle: (Int) -> Unit,
    onSetSelectedIds: (Set<Int>) -> Unit
) {
    val allIds = remember(members) { members.map { it.first }.toSet() }
    val hasMembers = allIds.isNotEmpty()
    val allSelected = hasMembers && selectedIds.containsAll(allIds)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painterResource(R.drawable.ic_group_schedule),
                contentDescription = null,
                tint = TextBlack
            )
            Spacer(Modifier.width(16.dp))

            Text(
                "참여자",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoleChip(
                label = "전체",
                color = Main,
                selected = allSelected,
                enabled = hasMembers,
                onClick = {
                    val next = if (allSelected) emptySet() else allIds
                    onSetSelectedIds(next)
                }
            )

            members.forEach { (id, roleLabel) ->
                val (role, order) = remember(roleLabel) { parseRoleAndOrder(roleLabel) }
                val color = remember(role, order) { role.getColor(order) }
                val selected = selectedIds.contains(id)
                RoleChip(
                    label = roleLabel,
                    color = color,
                    selected = selected,
                    onClick = { onToggle(id) }
                )
            }
        }
    }
}

// 그룹 맴버 칩
@Composable
private fun RoleChip(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(if (selected) color.copy(alpha = 0.5f) else ButtonDisabled)
            .then(
                if (selected)
                    Modifier
                        .border(width = 1.dp, color = color, shape = RoundedCornerShape(100))
                else
                    Modifier
                        .border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(100))
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
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
            color = if (selected) TextBlack else TextWhite,
            maxLines = 1
        )
    }
}