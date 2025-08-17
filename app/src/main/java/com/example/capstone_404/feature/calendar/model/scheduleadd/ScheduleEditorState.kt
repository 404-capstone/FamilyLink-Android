package com.example.capstone_404.feature.calendar.model.scheduleadd

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class ScheduleEditorState(
    val isAllDay: Boolean = false,
    val startMillis: Long = System.currentTimeMillis(),
    val endMillis: Long = System.currentTimeMillis() + 60 * 60 * 1000
)

// 날짜 & 시간 기본 설정
fun ScheduleEditorState.presetFromDate(
    date: LocalDate,
    zone: ZoneId = ZoneId.systemDefault(),
    startHour: Int = 10,
    durationMinutes: Int = 60
): ScheduleEditorState {
    val base = ZonedDateTime.of(date, LocalTime.of(startHour, 0), zone)
    return copy(
        isAllDay = false,
        startMillis = base.toInstant().toEpochMilli(),
        endMillis = base.plusMinutes(durationMinutes.toLong()).toInstant().toEpochMilli()
    )
}
// 날짜 & 시간 변경
fun ScheduleEditorState.withRange(start: Long, end: Long) = copy(startMillis = start, endMillis = end)
// 종일 적용
fun ScheduleEditorState.withAllDay(
    enabled: Boolean,
    zone: ZoneId = ZoneId.systemDefault()
): ScheduleEditorState {
    if (!enabled) return copy(isAllDay = false)
    val startDate = Instant.ofEpochMilli(startMillis).atZone(zone).toLocalDate()
    val endDate = Instant.ofEpochMilli(endMillis).atZone(zone).toLocalDate()
    val newStart = LocalDateTime.of(startDate, LocalTime.MIN).atZone(zone).toInstant().toEpochMilli()
    val newEnd = LocalDateTime.of(endDate, LocalTime.MAX.minusSeconds(1)).atZone(zone).toInstant().toEpochMilli()
    return copy(isAllDay = true, startMillis = newStart, endMillis = newEnd)
}