package com.example.capstone_404.feature.calendar.model

import com.example.capstone_404.data.retrofit.model.response.ScheduleData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun scheduleMapper(
    scheduleData: ScheduleData,
    userIdToRole: Map<Int, String>,
    currentUserId: Int?
): Map<LocalDate, List<Schedule>> {
    val result = mutableMapOf<LocalDate, MutableList<Schedule>>()
    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    // 하루 이상 일정 해당 모든 날짜에 등록
    fun putSpanning(schedule: Schedule) {
        val start = schedule.startTime
        val end = schedule.endTime.let { if (it.isBefore(start)) start else it }
        var day = start.toLocalDate()
        val last = end.toLocalDate()
        while (!day.isAfter(last)) {
            result.getOrPut(day) { mutableListOf() }.add(schedule)
            day = day.plusDays(1)
        }
    }

    // 개인 일정
    scheduleData.personalUserSchedule.forEach { user ->
        user.personalSchedule.forEach { data ->
            val isPrivate = data.permission
            val writerId = user.userid
            val writerRole = userIdToRole[writerId] ?: "알 수 없음"

            val title = if (isPrivate && currentUserId != null && currentUserId != writerId) {
                "${writerRole}의 일정"
            } else {
                data.title
            }

            val schedule = Schedule(
                id = data.scheduleid,
                title = title,
                startTime = LocalDateTime.parse(data.start_time, dateFormat),
                endTime = LocalDateTime.parse(data.end_time, dateFormat),
                writerId = user.userid,
                isGroup = false,
                isTimeFlexible = data.timeflex,
                participantUserIds = listOf(user.userid)
            )
            putSpanning(schedule)
        }
    }

    // 그룹 일정
    scheduleData.groupSchedule.forEach { data ->
        val schedule = Schedule(
            id = data.scheduleId,
            title = data.title,
            startTime = LocalDateTime.parse(data.startTime, dateFormat),
            endTime = LocalDateTime.parse(data.endTime, dateFormat),
            writerId = null,
            isGroup = true,
            isTimeFlexible = data.timeflex,
            participantUserIds = data.participants
        )
        putSpanning(schedule)
    }
    return result.mapValues { (_, list) ->
        list.sortedBy { it.startTime }
    }
}