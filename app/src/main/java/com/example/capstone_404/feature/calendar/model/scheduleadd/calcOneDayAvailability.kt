package com.example.capstone_404.feature.calendar.model.scheduleadd

import com.example.capstone_404.feature.calendar.model.Schedule
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId

enum class DayAvailability { SELECTABLE, OPTIMIZATION, BLOCKED }

// 최적화 가능 유무 계산 함수
fun calcOneDayAvailability(
    centerMonth: YearMonth,
    editor: ScheduleEditorState,
    selectedMemberIds: Set<Int>,
    schedulesByDate: Map<LocalDate, List<Schedule>>,
    zoneId: ZoneId
): Map<LocalDate, DayAvailability> {
    // 범위 설정
    val startMonth = centerMonth
    val endMonth   = centerMonth.plusMonths(3)

    // 시작 시간 추출
    val startTime = if (editor.isAllDay) {
        LocalTime.MIN
    } else {
        Instant.ofEpochMilli(editor.startMillis).atZone(zoneId).toLocalTime()
    }
    // 종료 시간 추출
    val endTime = if (editor.isAllDay) {
        LocalTime.MAX.minusSeconds(1)
    } else {
        Instant.ofEpochMilli(editor.endMillis).atZone(zoneId).toLocalTime()
    }

    // 빈 결과 리스트
    val result = LinkedHashMap<LocalDate, DayAvailability>()

    var month = startMonth
    while (!month.isAfter(endMonth)) {
        var day = month.atDay(1)
        val lastDay = month.atEndOfMonth()
        while (!day.isAfter(lastDay)) {
            val slotStart = LocalDateTime.of(day, startTime)
            val slotEnd = LocalDateTime.of(day, endTime)
            // 최적화 불가
            var hasNoTimeFlex = false
            var hasTimeFlex  = false

            val daySchedules = schedulesByDate[day].orEmpty()

            daySchedules.forEach { schedule ->

                val relation = schedule.participantUserIds.orEmpty().any { it in selectedMemberIds }
                if (!relation) return@forEach

                val sStart = schedule.startTime
                val sEnd   = schedule.endTime

                if (timeComparison(slotStart, slotEnd, sStart, sEnd)) {
                    if (schedule.isTimeFlexible) {
                        hasTimeFlex = true
                    } else {
                        hasNoTimeFlex = true
                    }
                }
            }

            val availability = when {
                hasNoTimeFlex -> DayAvailability.BLOCKED
                hasTimeFlex -> DayAvailability.OPTIMIZATION
                else -> DayAvailability.SELECTABLE
            }
            result[day] = availability

            day = day.plusDays(1)
        }
        month = month.plusMonths(1)
    }
    return result
}

private fun timeComparison(
    aStart: LocalDateTime,
    aEnd: LocalDateTime,
    bStart: LocalDateTime,
    bEnd: LocalDateTime
): Boolean {
    // 비교 결과 반환
    return aStart < bEnd && aEnd > bStart
}