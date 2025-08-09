package com.example.capstone_404.feature.calendar.model

import java.time.LocalDateTime

data class Schedule(
    val id: Int,
    val title: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val writerId: Int?,
    val isGroup: Boolean,
    val isTimeFlexible: Boolean,
    val participantUserIds: List<Int>? = null
)