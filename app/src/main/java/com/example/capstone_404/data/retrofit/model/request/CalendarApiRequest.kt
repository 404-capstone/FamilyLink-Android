package com.example.capstone_404.data.retrofit.model.request

// 개인 일정 추가 Requset Body
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