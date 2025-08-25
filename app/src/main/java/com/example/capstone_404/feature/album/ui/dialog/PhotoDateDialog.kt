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
import java.util.Calendar
import kotlin.math.roundToInt

@Composable
fun PhotoDateDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateParts = initialDate.split("-")
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    var selectedYear by remember {
        mutableIntStateOf(
            if (dateParts.size == 3) dateParts[0].toIntOrNull() ?: currentYear
            else currentYear
        )
    }
    var selectedMonth by remember {
        mutableIntStateOf(
            if (dateParts.size == 3) dateParts[1].toIntOrNull() ?: currentMonth
            else currentMonth
        )
    }
    var selectedDay by remember {
        mutableIntStateOf(
            if (dateParts.size == 3) dateParts[2].toIntOrNull() ?: currentDay
            else currentDay
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "촬영 날짜 선택",
                style = MaterialTheme.typography.titleMedium,
                color = TextBlack
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 선택된 날짜 미리보기
                val selectedDate = LocalDate.of(selectedYear, selectedMonth, selectedDay)
                PhotoPreview(
                    date = selectedDate,
                    time = null
                )
                // 년/월/일 휠 피커
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 년도 선택 (2000년~현재년도)
                    PhotoWheelPicker(
                        items = (2000..currentYear).map { "${it}년" },
                        initialIndex = selectedYear - 2000,
                        onSelectedIndexChanged = { index ->
                            selectedYear = 2000 + index
                            // 년도가 변경되면 일자 범위 조정
                            val maxDay = getMaxDayOfMonth(selectedYear, selectedMonth)
                            if (selectedDay > maxDay) {
                                selectedDay = maxDay
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // 월 선택
                    PhotoWheelPicker(
                        items = (1..12).map { "${it}월" },
                        initialIndex = selectedMonth - 1,
                        onSelectedIndexChanged = { index ->
                            selectedMonth = index + 1
                            // 월이 변경되면 일자 범위 조정
                            val maxDay = getMaxDayOfMonth(selectedYear, selectedMonth)
                            if (selectedDay > maxDay) {
                                selectedDay = maxDay
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // 일 선택
                    PhotoWheelPicker(
                        items = (1..getMaxDayOfMonth(selectedYear, selectedMonth)).map { "${it}일" },
                        initialIndex = selectedDay - 1,
                        onSelectedIndexChanged = { index ->
                            selectedDay = index + 1
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
                    val formattedDate = String.format(
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )
                    onDateSelected(formattedDate)
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

// 월의 최대 일수 계산
private fun getMaxDayOfMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}