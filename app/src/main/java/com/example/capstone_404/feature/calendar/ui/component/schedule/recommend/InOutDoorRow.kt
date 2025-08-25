package com.example.capstone_404.feature.calendar.ui.component.schedule.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.schedule.recommend.InOutDoor
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.ModeItem
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun InOutDoorRow(
    selected: InOutDoor?,
    onChange: (InOutDoor?) -> Unit
) {
    val indoorActive = selected == InOutDoor.INDOOR || selected == InOutDoor.ALL
    val outdoorActive = selected == InOutDoor.OUTDOOR || selected == InOutDoor.ALL

    fun inOutDoor(nextIndoor: Boolean, nextOutdoor: Boolean): InOutDoor? = when {
        nextIndoor && nextOutdoor -> InOutDoor.ALL
        nextIndoor -> InOutDoor.INDOOR
        nextOutdoor -> InOutDoor.OUTDOOR
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp)
    ) {
        Text(
            text = "다중 선택 가능",
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 실내 토글
            ModeItem(
                text = "실내",
                active = indoorActive,
                onClick = {
                    val value = inOutDoor(!indoorActive, outdoorActive)
                    onChange(value)
                },
                modifier = Modifier.weight(1f)
            )
            // 실외 토글
            ModeItem(
                text = "실외",
                active = outdoorActive,
                onClick = {
                    val value = inOutDoor(indoorActive, !outdoorActive)
                    onChange(value)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}