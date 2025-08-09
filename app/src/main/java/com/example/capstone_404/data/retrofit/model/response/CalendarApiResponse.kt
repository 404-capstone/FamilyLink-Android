package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 일정 전체 조회 Response
@Serializable
data class ScheduleData(
    val groupId: Int,
    val personalUserSchedule: List<PersonalUserScheduleData>,
    val groupSchedule: List<GroupScheduleData>
)
@Serializable
data class PersonalUserScheduleData(
    val userid: Int,
    val personalSchedule: List<PersonalScheduleItemDto>
)
@Serializable
data class PersonalScheduleItemDto(
    val scheduleid: Int,
    val permission: Boolean,
    val isTimeFlexible: Boolean,
    val title: String,
    val start_time: String,
    val end_time: String
)
@Serializable
data class GroupScheduleData(
    val scheduleId: Int,
    val isTimeFlexible: Boolean,
    val title: String,
    val startTime: String,
    val endTime: String,
    val groupUserId: List<Int>
)