package com.example.capstone_404.feature.calendar.model.schedule.edit

import com.example.capstone_404.feature.calendar.model.schedule.add.FamilyMode
import com.example.capstone_404.feature.calendar.model.schedule.add.ScheduleEditorState

data class EditState(
    val scheduleId: Int,
    val isGroup: Boolean,
    val title: String = "",
    val isFlexible: Boolean? = null,
    val editor: ScheduleEditorState = ScheduleEditorState(),
    val location: String = "",
    val memo: String = "",
    val familyMode: FamilyMode? = null
)