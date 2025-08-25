package com.example.capstone_404.feature.calendar.ui.dialog

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.model.TimeTarget
import com.example.capstone_404.feature.calendar.model.dateFormatter
import com.example.capstone_404.feature.calendar.model.timeFormatter
import com.example.capstone_404.feature.calendar.ui.component.schedule.add.DateOnlyCalendar
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextGray
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.OutDateStyle
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

@SuppressLint("UnrememberedMutableState")
@Composable
fun ScheduleDateDialog(
    target: TimeTarget,
    allDay: Boolean,
    startMillis: Long,
    endMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault(),
    onDismiss: () -> Unit,
    onConfirm: (newStartMillis: Long, newEndMillis: Long) -> Unit,
    onFamily: Boolean = false
) {
    val context = LocalContext.current

    // 프리뷰 날짜 시간 초기 값
    val startInit = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDateTime()
    val endInit   = Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDateTime()
    val initialDate = if (target == TimeTarget.START) startInit.toLocalDate() else endInit.toLocalDate()
    // 달력 범위
    val startMonth = remember { YearMonth.now().minusMonths(6) }
    val endMonth   = remember { YearMonth.now().plusMonths(6) }
    // 캘린더 상태 + 선택 상태
    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = YearMonth.from(initialDate),
        firstDayOfWeek = DayOfWeek.SUNDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    // 선택된 날짜
    var pickedDate by remember { mutableStateOf(initialDate) }

    // 날짜&시간 갱신
    val previewStart by derivedStateOf {
        if (target == TimeTarget.START)
            LocalDateTime.of(pickedDate, if (allDay) LocalTime.MIN else startInit.toLocalTime())
        else startInit
    }
    val previewEnd by derivedStateOf {
        if (target == TimeTarget.END)
            LocalDateTime.of(pickedDate, if (allDay) LocalTime.MAX.minusSeconds(1) else endInit.toLocalTime())
        else endInit
    }
    // 가족 - 며칠일 때 2일 이상 검증
    val gapDay = remember(target, onFamily) {
        if (onFamily && target == TimeTarget.END) 1 else 0
    }
    val endDate = remember(startInit, gapDay) {
        startInit.toLocalDate().plusDays(gapDay.toLong())
    }
    val isPickEnabled: (LocalDate) -> Boolean = remember(target, endDate) {
        if (target == TimeTarget.END && gapDay > 0) { date: LocalDate -> !date.isBefore(endDate) }
        else { _: LocalDate -> true }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (target == TimeTarget.START) "시작 날짜 선택" else "종료 날짜 선택",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Column {
                // 상단 프리뷰
                RangePreview(
                    start = previewStart,
                    end = previewEnd,
                    active = target,
                    allDay = allDay
                )
                // 날짜 선택용 캘린더
                DateOnlyCalendar(
                    calendarState = calendarState,
                    selectedDate = pickedDate,
                    onDateSelected = { pickedDate = it }
                )
            }
        },
        confirmButton = {
            ButtonDefault(
                text = "선택",
                onClick = {
                    if (!isPickEnabled(pickedDate)) {
                        Toast.makeText(context, "\"며칠\" 일정은 시작과 종료가 최소 1일 이상 차이나야 합니다.", Toast.LENGTH_SHORT).show()
                        return@ButtonDefault
                    }

                    val newStart = previewStart
                    var newEnd   = previewEnd

                    if (target == TimeTarget.START) {
                        if (!newEnd.isAfter(newStart)) {
                            newEnd = if (allDay) {
                                LocalDateTime.of(
                                    newStart.toLocalDate(),
                                    LocalTime.MAX.minusSeconds(1)
                                )
                            } else {
                                newStart.plusHours(1)
                            }
                        }
                    } else {
                        val sameDay = newEnd.toLocalDate() == newStart.toLocalDate()
                        if (sameDay && !newEnd.isAfter(newStart)) {
                            val plus = newStart.plusHours(1)
                            newEnd = if (plus.toLocalDate() == newStart.toLocalDate()) {
                                plus
                            } else {
                                LocalDateTime.of(newStart.toLocalDate(), LocalTime.MAX.minusSeconds(1))
                            }
                        }

                        if (!newEnd.isAfter(newStart)) {
                            Toast.makeText(context, "종료 시점은 시작 시점 이후로 설정해 주세요.", Toast.LENGTH_SHORT).show()
                            return@ButtonDefault
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

// 날짜 & 시간 미리보기(공용)
@Composable
fun RangePreview(
    start: LocalDateTime,
    end: LocalDateTime,
    active: TimeTarget,
    allDay: Boolean
) {
    Row(
        Modifier.fillMaxWidth()
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = dateFormatter.format(start.toLocalDate()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.START) Main else TextGray
            )
            Text(
                text = if (allDay) "종일" else timeFormatter.format(start.toLocalTime()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.START) Main else TextGray
            )
        }

        Icon(
            painter = painterResource(R.drawable.ic_next),
            tint = TextGray,
            contentDescription = null)

        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = dateFormatter.format(end.toLocalDate()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.END) Main else TextGray
            )
            Text(
                text = if (allDay) "종일" else timeFormatter.format(end.toLocalTime()),
                style = MaterialTheme.typography.headlineSmall,
                color = if (active == TimeTarget.END) Main else TextGray
            )
        }
    }
}