package com.example.capstone_404.feature.album.ui.component

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

@Composable
fun ParticipantSelector(
    members: List<Pair<Int, String>>,      // 그룹 멤버 정보
    selectedParticipants: Set<Int>,
    onParticipantToggle: (Int) -> Unit,
    onSetSelectedParticipants: (Set<Int>) -> Unit,  // 전체 선택/해제용
    modifier: Modifier = Modifier
) {
    val allIds = remember(members) { members.map { it.first }.toSet() }
    val hasMembers = allIds.isNotEmpty()
    val allSelected = hasMembers && selectedParticipants.containsAll(allIds)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // 제목
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = TextBlack
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "참여자",
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack
            )
        }

        Spacer(Modifier.height(8.dp))

        // 참여자 선택
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "전체" 선택 chip
            RoleChip(
                label = "전체",
                color = Main,
                selected = allSelected,
                enabled = hasMembers,
                onClick = {
                    val next = if (allSelected) emptySet() else allIds
                    onSetSelectedParticipants(next)
                }
            )
            // 개별 멤버 chip
            members.forEach { (id, roleLabel) ->
                val splitRole = parseRoleAndOrder(roleLabel)
                val color = remember(splitRole) { splitRole.first.getColor(splitRole.second) }
                val selected = selectedParticipants.contains(id)
                RoleChip(
                    label = roleLabel,
                    color = color,
                    selected = selected,
                    onClick = { onParticipantToggle(id) }
                )
            }
        }
    }
}

// RoleChip 컴포넌트
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
            .background(if (selected) color.copy(alpha = 0.5f) else Color.White)
            .then(
                if (selected)
                    Modifier.border(width = 1.dp, color = color, shape = RoundedCornerShape(100))
                else
                    Modifier.border(width = 1.dp, color = Stroke, shape = RoundedCornerShape(100))
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
            color = if (selected) TextBlack else TextBlack,
            maxLines = 1
        )
    }
}