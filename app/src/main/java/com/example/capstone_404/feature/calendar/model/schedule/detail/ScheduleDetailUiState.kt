package com.example.capstone_404.feature.calendar.model.schedule.detail

import com.example.capstone_404.data.retrofit.model.response.ScheduleDetailData

data class ScheduleDetailUiState(
    val isLoading: Boolean = false,
    val data: ScheduleDetailData? = null,
    val error: String? = null
)