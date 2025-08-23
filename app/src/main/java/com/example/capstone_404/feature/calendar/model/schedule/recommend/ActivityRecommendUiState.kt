package com.example.capstone_404.feature.calendar.model.schedule.recommend

import com.example.capstone_404.feature.calendar.model.schedule.add.ScheduleEditorState

data class TypeGroup(val selected: ActivityType? = null)

data class ActivityRecommendUiState(
    val area: String? = null,
    val editor: ScheduleEditorState = ScheduleEditorState(),
    val memberIds: Set<Int> = emptySet(),
    val inOutDoor: InOutDoor? = null,
    val typeGroups: List<TypeGroup> = listOf(TypeGroup())
) {
    // 그룹 추가 활성화 조건
    val canAddTypeGroup: Boolean
        get() = typeGroups.size < 3 && typeGroups.last().selected != null

    // 버튼 활성화 조건
    val isValid: Boolean
        get() = !area.isNullOrBlank()
                && !editor.isAllDay
                && editor.startMillis < editor.endMillis
                && memberIds.isNotEmpty()
                && inOutDoor != null
                && typeGroups.isNotEmpty()
                && typeGroups.all { it.selected != null }
}