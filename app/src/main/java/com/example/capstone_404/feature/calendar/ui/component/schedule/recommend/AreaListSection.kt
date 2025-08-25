package com.example.capstone_404.feature.calendar.ui.component.schedule.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.schedule.recommend.Area
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun AreaColumn(
    title: String,
    items: List<Area>,
    selected: Area?,
    onSelect: (Area) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        // 시·도 | 군·구 타이틀
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.White)
                .border(0.5.dp, Stroke),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlack,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(2.dp))
        // 리스트
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            items(items) { area ->
                AreaRow(
                    label = area.short,
                    selected = area.full == selected?.full,
                    onClick = { onSelect(area) }
                )
                Spacer(Modifier.height(1.dp))
            }
        }
    }
}

@Composable
fun AreaRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(if (selected) Main.copy(alpha = 0.1f) else Color.White)
            .border(0.5.dp, if (selected) Main else Stroke)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) Main else TextBlack
        )
    }
}