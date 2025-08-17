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
    val personalSchedule: List<PersonalScheduleItemData>
)
@Serializable
data class PersonalScheduleItemData(
    val scheduleid: Int,
    val permission: Boolean,
    val timeflex: Boolean,
    val title: String,
    val start_time: String,
    val end_time: String
)
@Serializable
data class GroupScheduleData(
    val scheduleId: Int,
    val timeflex: Boolean,
    val title: String,
    val startTime: String,
    val endTime: String,
    val participants: List<Int>
)

// 개인 일정 추가 Response
@Serializable
data class AddPersonalData(
    val id: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val content: String? = null,
    val permission: Boolean,
    val timeflex: Boolean
)

// 일정 최적화 Response
@Serializable
data class OptimizeData(
    val groupId: Int,
    val beforeSchedule: OptimizeBefore,
    val afterSchedule: OptimizeAfter
)
@Serializable
data class OptimizeBefore(
    val personalSchedule: List<OptimizePersonalItem>
)
@Serializable
data class OptimizeAfter(
    val personalSchedule: List<OptimizePersonalItem>,
    val groupSchedule: OptimizeGroupItem? = null
)
@Serializable
data class OptimizePersonalItem(
    val title: String,
    val memberId: Int,
    val memberPosition: String,
    val schduleId: Int,
    val startTime: String,
    val endTime: String
)
@Serializable
data class OptimizeGroupItem(
    val startTime: String,
    val endTime: String
)

// 가족 일정 추가 Response
@Serializable
data class AddGroupData(
    val scheduleId: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val content: String? = null,
    val location: String? = null,
    val timeflex: Boolean,
    val participants: List<Int>,
    val calendarId: Int
)

// 일정 수정 Response
@Serializable
data class EditScheduleData(
    val id: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val content: String?,
    val location: String?,
    val timeflex: Boolean
)