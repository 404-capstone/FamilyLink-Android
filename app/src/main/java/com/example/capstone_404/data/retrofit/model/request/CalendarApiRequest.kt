package com.example.capstone_404.data.retrofit.model.request

// 개인 일정 추가 Request Body
data class AddPersonalScheduleRequest(
    val title: String,
    val permission: Boolean,
    val timeflex: Boolean,
    val startTime: String,
    val endTime: String,
    val location: String? = null,
    val content: String? = null,
    val groupId: Int
)

// 일정 최적화 Request Body
data class OptimizeRequest(
    val groupId: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val memberIds: List<Int>,
    val date: String
)

// 가족 일정 추가 Request Body
data class AddGroupScheduleRequest(
    val title: String,
    val startTime: String,
    val endTime: String,
    val content: String? = null,
    val groupId: Int,
    val location: String? = null,
    val timeflex: Boolean,
    val participants: List<Int>
)

// 일정 수정 Request Body
data class EditScheduleRequest(
    val id: Int,
    val title: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val content: String? = null,
    val location: String? = null,
    val timeflex: Boolean? = null,
    val participantIds: List<Int>? = null,
    val groupId: Int
)