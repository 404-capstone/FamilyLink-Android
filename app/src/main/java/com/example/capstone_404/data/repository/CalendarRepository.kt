package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.ScheduleData

// 캘린더 관련 API Repository 인터페이스
interface CalendarRepository {

    // 일정 전체 조회
    suspend fun getAllSchedule(
        groupId: Int
    ): Result<ScheduleData>

    // 개인 일정 추가
    suspend fun addPersonalSchedule(
        body: AddPersonalScheduleRequest
    ): Result<AddPersonalData>
}