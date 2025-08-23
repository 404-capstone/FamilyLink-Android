package com.example.capstone_404.feature.calendar.ui.component.schedule.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.schedule.recommend.InOutDoor
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ModeItem
import com.example.capstone_404.ui.theme.Stroke

@Composable
fun InOutDoorRow(
    selected: InOutDoor?,
    onSelect: (InOutDoor) -> Unit
) {
    Column(
        modifier = Modifier
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
                text = "실내",
                active = selected == InOutDoor.INDOOR,
                onClick = { onSelect(InOutDoor.INDOOR) },
                modifier = Modifier.weight(1f)
            )
            ModeItem(
                text = "실외",
                active = selected == InOutDoor.OUTDOOR,
                onClick = { onSelect(InOutDoor.OUTDOOR) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}