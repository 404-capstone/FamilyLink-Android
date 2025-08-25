package com.example.capstone_404.feature.calendar.ui.dialog

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.TimeTarget
import com.example.capstone_404.feature.calendar.model.timeFormatter
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@Composable
fun ScheduleTimeDialog(
    target: TimeTarget,
    startMillis: Long,
    endMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault(),
    onDismiss: () -> Unit,
    onConfirm: (newStartMillis: Long, newEndMillis: Long) -> Unit,
    onlyTime: Boolean = false
) {
    val context = LocalContext.current

    // 프리뷰 초기 값
    val startInit = remember(startMillis, zoneId) {
        Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDateTime()
    }
    val endInit = remember(endMillis, zoneId) {
        Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDateTime()
    }
    // 선택된 시간
    var pickedTime by remember {
        mutableStateOf(if (target == TimeTarget.START) startInit.toLocalTime() else endInit.toLocalTime())
    }

    // 시간 갱신
    val previewStart by derivedStateOf {
        if (target == TimeTarget.START)
            LocalDateTime.of(startInit.toLocalDate(), pickedTime)
        else startInit
    }
    val previewEnd by derivedStateOf {
        if (target == TimeTarget.END)
            LocalDateTime.of(endInit.toLocalDate(), pickedTime)
        else endInit
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (target == TimeTarget.START) "시작 시간 선택" else "종료 시간 선택",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 상단 프리뷰
                if (onlyTime) {
                    TimeOnlyPreview(
                        start = previewStart,
                        end = previewEnd,
                        active = target
                    )
                } else {
                    RangePreview(
                        start = previewStart,
                        end = previewEnd,
                        active = target,
                        allDay = false
                    )
                }
                // 시간 선택 다이얼
                TimeWheelRow(
                    time = pickedTime,
                    onTimeChange = { pickedTime = it }
                )
            }
        },
        confirmButton = {
            ButtonDefault(
                text = "선택",
                onClick = {
                    val newStart = previewStart
                    var newEnd   = previewEnd

                    if (onlyTime) {
                        if (target == TimeTarget.START) {
                            val startLt = newStart.toLocalTime()
                            if (startLt.hour == 23 && startLt.minute == 59) {
                                Toast.makeText(context, "시작 시간은 23:59로 설정할 수 없어요.", Toast.LENGTH_SHORT).show()
                                return@ButtonDefault
                            }
                            if (startLt.hour >= 23) {
                                newEnd = LocalDateTime.of(newStart.toLocalDate(), LocalTime.of(23, 59))
                            } else {
                                if (!newEnd.isAfter(newStart)) {
                                    newEnd = newStart.plusHours(1)
                                }
                            }
                        } else {
                            if (!newEnd.isAfter(newStart)) {
                                Toast.makeText(context, "종료 시점은 시작 시점 이후로 설정해 주세요.", Toast.LENGTH_SHORT).show()
                                return@ButtonDefault
                            }
                        }
                    } else {
                        if (target == TimeTarget.START) {
                            if (!newEnd.isAfter(newStart)) {
                                newEnd = newStart.plusHours(1)
                            }
                        } else {
                            if (!newEnd.isAfter(newStart)) {
                                Toast.makeText(context, "종료 시점은 시작 시점 이후로 설정해 주세요.", Toast.LENGTH_SHORT).show()
                                return@ButtonDefault
                            }
                        }
                    }
                    onConfirm(
                        newStart.atZone(zoneId).toInstant().toEpochMilli(),
                        newEnd.atZone(zoneId).toInstant().toEpochMilli()
                    )
                    onDismiss()
                }
            )
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = Color.White
    )
}

// <오전|오후> <시> <분> Row
@Composable
private fun TimeWheelRow(
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val hours = remember { listOf(12) + (1..11).toList() }
    val hourLabels = remember { hours.map { it.toString().padStart(2, '0') } }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    var apIndex by remember { mutableIntStateOf(if (time.hour < 12) 0 else 1) }
    var hourIndex by remember { mutableIntStateOf(hours.indexOf(hourCalc(time.hour))) }
    var minuteIndex by remember { mutableIntStateOf(time.minute.coerceIn(0, 59)) }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 오전|오후
        WheelPicker(
            items = listOf("오전", "오후"),
            initialIndex = apIndex,
            onSelectedIndexChanged = {
                apIndex = it
                onTimeChange(changeTo24(apIndex, hours[hourIndex], minuteIndex))
            },
            modifier = Modifier.weight(1f)
        )
        // 시
        WheelPicker(
            items = hourLabels,
            initialIndex = hourIndex,
            onSelectedIndexChanged = {
                hourIndex = it
                onTimeChange(changeTo24(apIndex, hours[hourIndex], minuteIndex))
            },
            modifier = Modifier.weight(1f)
        )
        // 분
        WheelPicker(
            items = minutes,
            initialIndex = minuteIndex,
            onSelectedIndexChanged = {
                minuteIndex = it
                onTimeChange(changeTo24(apIndex, hours[hourIndex], minuteIndex))
            },
            modifier = Modifier.weight(1f)
        )
    }
}

// 12시간 분할 출력용
private fun hourCalc(hour24: Int): Int = (hour24 % 12).let { if (it == 0) 12 else it }
// 24시 기준 변환
private fun changeTo24(apIndex: Int, hour12: Int, minute: Int): LocalTime {
    val base = if (apIndex == 0) 0 else 12
    val h24 = (if (hour12 == 12) 0 else hour12) + base
    return LocalTime.of(h24, minute)
}

@Composable
private fun WheelPicker(
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

    // 시간 변화 감지
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
                        maxLines = 1
                    )
                }
            }
        }
        HorizontalDivider(
            Modifier.align(Alignment.Center).fillMaxWidth().offset(y = -(itemHeight / 2)).height(1.dp),
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
            Modifier.align(Alignment.Center).fillMaxWidth().offset(y =  (itemHeight / 2)).height(1.dp),
            color = Stroke
        )
    }
}

@Composable
private fun TimeOnlyPreview(
    start: LocalDateTime,
    end: LocalDateTime,
    active: TimeTarget
) {
    Row(
        Modifier
            .fillMaxWidth()
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = timeFormatter.format(start.toLocalTime()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.START) Main else TextGray
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_next),
            tint = TextGray,
            contentDescription = null
        )

        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = timeFormatter.format(end.toLocalTime()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.END) Main else TextGray
            )
        }
    }
}