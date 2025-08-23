package com.example.capstone_404.feature.calendar.ui.component.schedule.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import com.example.capstone_404.feature.calendar.model.schedule.recommend.ActivityType
import com.example.capstone_404.feature.calendar.model.schedule.recommend.TypeGroup
import com.example.capstone_404.feature.calendar.model.schedule.recommend.iconRes
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun TypeGroupSection(
    typeGroups: List<TypeGroup>,
    onSelect: (groupIndex: Int, type: ActivityType) -> Unit,
    onRemoveGroup: (groupIndex: Int) -> Unit,
    canAddGroup: Boolean = false,
    onAddGroup: () -> Unit = {}
) {
    val alreadySelected: Set<ActivityType> =
        remember(typeGroups) { typeGroups.mapNotNull { it.selected }.toSet() }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        typeGroups.forEachIndexed { index, group ->
            TypeGroupCard(
                index = index,
                group = group,
                onSelect = onSelect,
                canRemove = typeGroups.size > 1,
                onRemove = onRemoveGroup,
                showAddBar = (index == typeGroups.lastIndex && canAddGroup),
                onAddGroup = onAddGroup,
                alreadySelected = alreadySelected
            )
        }
    }
}

@Composable
private fun TypeGroupCard(
    index: Int,
    group: TypeGroup,
    onSelect: (groupIndex: Int, trait: ActivityType) -> Unit,
    canRemove: Boolean,
    onRemove: (groupIndex: Int) -> Unit,
    showAddBar: Boolean,
    onAddGroup: () -> Unit,
    alreadySelected: Set<ActivityType>
) {
    val types: List<ActivityType> = ActivityType.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 8개 아이템 리스트
            val items = buildList {
                addAll(types)
                add(null)
            }

            items.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { item ->
                        if (item == null) {
                            // 활동 성격 그룹 삭제
                            GridCell(
                                painterId = R.drawable.ic_withdraw,
                                label = "삭제",
                                tint = if (!canRemove) Stroke else Error,
                                selected = false,
                                onClick = { if (canRemove) onRemove(index) },
                                enabled = canRemove,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            val selected = (group.selected == item)
                            val takenByOthers = item in alreadySelected && !selected
                            // 활동 성격 셀
                            GridCell(
                                painterId = item.iconRes(),
                                label = item.label,
                                tint = when {
                                    selected -> Main
                                    takenByOthers -> Stroke
                                    else -> TextBlack
                                },
                                selected = selected,
                                onClick = {
                                    if (!selected && !takenByOthers) onSelect(index, item)
                                },
                                enabled = !takenByOthers,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
        // 활동 성격 추가
        if (showAddBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(Main, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .clickable { onAddGroup() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "활동 성격 추가(최대 3개)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite
                )
            }
        }
    }
}

// 활동 성격 셀
@Composable
private fun GridCell(
    painterId: Int,
    label: String,
    tint: Color,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .widthIn(min = 0.dp)
            .padding(vertical = 6.dp)
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(painterId),
            contentDescription = null,
            tint = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = when {
                selected -> Main
                enabled -> TextBlack
                else -> Stroke
            },
            maxLines = 1
        )
    }
}