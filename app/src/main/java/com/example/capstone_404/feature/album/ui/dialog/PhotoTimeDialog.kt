package com.example.capstone_404.feature.album.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.capstone_404.feature.album.ui.component.PhotoPreview
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun PhotoTimeDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = initialTime.split(":")
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

    val is24Hour = if (timeParts.size == 2) timeParts[0].toIntOrNull() ?: currentHour else currentHour
    val isAM = is24Hour < 12
    val hour12 = if (is24Hour == 0) 12 else if (is24Hour > 12) is24Hour - 12 else is24Hour

    var selectedAM by remember { mutableStateOf(isAM) }
    var selectedHour12 by remember { mutableIntStateOf(hour12) }
    var selectedMinute by remember {
        mutableIntStateOf(
            if (timeParts.size == 2) timeParts[1].toIntOrNull() ?: currentMinute
            else currentMinute
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "촬영 시간 선택",
                style = MaterialTheme.typography.titleMedium,
                color = TextBlack
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 선택된 시간 미리보기
                val hour24 = if (selectedAM) {
                    if (selectedHour12 == 12) 0 else selectedHour12
                } else {
                    if (selectedHour12 == 12) 12 else selectedHour12 + 12
                }
                val selectedTime = LocalTime.of(hour24, selectedMinute)
                val selectedDate = LocalDate.now()

                PhotoPreview(
                    date = selectedDate,
                    time = selectedTime
                )

                // 12시간 오전/오후 시/분 휠 피커
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 오전/오후 선택
                    PhotoWheelPicker(
                        items = listOf("오전", "오후"),
                        initialIndex = if (selectedAM) 0 else 1,
                        onSelectedIndexChanged = { index ->
                            selectedAM = index == 0
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // 시간 선택 (12, 1-11)
                    PhotoWheelPicker(
                        items = listOf("12") + (1..11).map { String.format("%02d", it) },
                        initialIndex = if (selectedHour12 == 12) 0 else selectedHour12,
                        onSelectedIndexChanged = { index ->
                            selectedHour12 = if (index == 0) 12 else index
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // 분 선택 (00-59)
                    PhotoWheelPicker(
                        items = (0..59).map { String.format("%02d", it) },
                        initialIndex = selectedMinute,
                        onSelectedIndexChanged = { index ->
                            selectedMinute = index
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            ButtonDefault(
                text = "선택",
                onClick = {
                    val hour24 = if (selectedAM) {
                        if (selectedHour12 == 12) 0 else selectedHour12
                    } else {
                        if (selectedHour12 == 12) 12 else selectedHour12 + 12
                    }
                    val formattedTime = String.format("%02d:%02d", hour24, selectedMinute)
                    onTimeSelected(formattedTime)
                    onDismiss()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = TextBlack,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

// 휠 피커
@Composable
private fun PhotoWheelPicker(
    items: List<String>,
    initialIndex: Int,
    onSelectedIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    itemHeight: Dp = 40.dp
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val fling = rememberSnapFlingBehavior(listState)
    val edgePadding = ((visibleCount - 1) / 2) * itemHeight

    val density = LocalDensity.current
    val itemHeightPx = remember(itemHeight, density) {
        with(density) { itemHeight.roundToPx() }.coerceAtLeast(1)
    }

    // 선택 변화 감지
    LaunchedEffect(listState, itemHeightPx) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .map { (index, offset) ->
                val offsetItems = (offset / itemHeightPx.toFloat()).roundToInt()
                (index + offsetItems).coerceIn(0, items.lastIndex)
            }
            .distinctUntilChanged()
            .collect { onSelectedIndexChanged(it) }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleCount)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = edgePadding),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(items) { _, label ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = TextBlack,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 선택 영역 강조
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .offset(y = -(itemHeight / 2))
                .height(1.dp),
            color = Stroke
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(Stroke.copy(alpha = 0.5f))
        )
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .offset(y = (itemHeight / 2))
                .height(1.dp),
            color = Stroke
        )
    }
}