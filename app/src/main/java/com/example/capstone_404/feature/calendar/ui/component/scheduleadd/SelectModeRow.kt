package com.example.capstone_404.feature.calendar.ui.component.scheduleadd

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.scheduleadd.FamilyMode
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun SelectModeRow(
    selected: FamilyMode,
    onSelect: (FamilyMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .selectableGroup()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeItem(
                text = "하루",
                active = selected == FamilyMode.ONE_DAY,
                onClick = { onSelect(FamilyMode.ONE_DAY) },
                modifier = Modifier.weight(1f)
            )
            ModeItem(
                text = "며칠",
                active = selected == FamilyMode.MULTI_DAYS,
                onClick = { onSelect(FamilyMode.MULTI_DAYS) },
                modifier = Modifier.weight(1f)
            )
        }
        if (selected == FamilyMode.ONE_DAY) {
            Spacer(Modifier.height(12.dp))

            GuideWarningCard(
                text = "하루 일정은 변동 가능한 개인 일정을 최적화하여 가능한 날짜를 제공합니다."
            )
        }
    }
}

@Composable
private fun ModeItem(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) Main else ButtonDisabled)
            .selectable(selected = active, onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextWhite
        )
    }
}