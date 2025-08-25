package com.example.capstone_404.feature.calendar.model.schedule.edit

import com.example.capstone_404.feature.calendar.model.schedule.add.FamilyMode
import com.example.capstone_404.feature.calendar.model.schedule.add.ScheduleEditorState
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

// 종일 여부 추출
fun extractAllDay(start: LocalDateTime, end: LocalDateTime): Boolean {
    val isMin = start.toLocalTime() == LocalTime.MIN
    val isMax = end.toLocalTime() == LocalTime.MAX.truncatedTo(ChronoUnit.MINUTES) || end.toLocalTime() == LocalTime.of(0, 0)
    return isMin && isMax
}

// 가족 일정 모드 추출
fun extractFamilyMode(start: LocalDateTime, end: LocalDateTime): FamilyMode =
    if (start.toLocalDate() == end.toLocalDate()) FamilyMode.ONE_DAY else FamilyMode.MULTI_DAYS

// 수정용 에디터
fun makeEditor(
    start: LocalDateTime,
    end: LocalDateTime,
    zone: ZoneId,
    isAllDay: Boolean
): ScheduleEditorState {
    val startMillis = start.atZone(zone).toInstant().toEpochMilli()
    val endMillis = end.atZone(zone).toInstant().toEpochMilli()
    return ScheduleEditorState(
        isAllDay = isAllDay,
        startMillis = startMillis,
        endMillis = endMillis
    )
}