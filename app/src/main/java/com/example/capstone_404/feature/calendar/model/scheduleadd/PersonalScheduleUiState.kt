package com.example.capstone_404.feature.calendar.model.scheduleadd

data class PersonalScheduleUiState(
    val title: String = "",
    val isTitlePrivate: Boolean = false,
    val isFlexible: Boolean = false,
    val editor: ScheduleEditorState = ScheduleEditorState(),
    val location: String = "",
    val memo: String = ""
)