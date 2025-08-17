package com.example.capstone_404.feature.calendar.model.scheduleadd

import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import java.time.LocalDate

enum class FamilyMode { ONE_DAY, MULTI_DAYS }

data class FamilyScheduleUiState(
    val title: String = "",
    val mode: FamilyMode = FamilyMode.ONE_DAY,
    val editor: ScheduleEditorState = ScheduleEditorState(),
    val location: String = "",
    val memo: String = "",
    val selectedMemberIds: Set<Int> = emptySet(),
    val isFlexible: Boolean = false,
    // 최적화 결과
    val optSelectedDate: LocalDate? = null,
    val optResult: OptimizeData? = null
)