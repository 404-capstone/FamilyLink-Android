package com.example.capstone_404.feature.calendar.model.schedule

// 일정 추가|수정 구분용
sealed interface FormMode {
    data object Add : FormMode
    data class Edit(val scheduleId: Int) : FormMode
}