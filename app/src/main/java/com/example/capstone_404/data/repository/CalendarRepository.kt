package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.request.AddGroupScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddScheduleCommentRequest
import com.example.capstone_404.data.retrofit.model.request.EditScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.OptimizeRequest
import com.example.capstone_404.data.retrofit.model.response.AddGroupData
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.AddScheduleCommentData
import com.example.capstone_404.data.retrofit.model.response.EditScheduleData
import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import com.example.capstone_404.data.retrofit.model.response.ScheduleData
import com.example.capstone_404.data.retrofit.model.response.ScheduleDetailData

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

    // 일정 최적화
    suspend fun optimize(
        body: OptimizeRequest
    ): Result<OptimizeData>

    // 가족 일정 추가
    suspend fun addGroupSchedule(
        body: AddGroupScheduleRequest
    ): Result<AddGroupData>

    // 일정 수정
    suspend fun editSchedule(
        body: EditScheduleRequest
    ): Result<EditScheduleData>

    // 일정 상세 조회
    suspend fun getScheduleDetail(
        scheduleId: Int
    ): Result<ScheduleDetailData>

    // 일정 댓글 작성
    suspend fun addScheduleComment(
        body: AddScheduleCommentRequest
    ): Result<AddScheduleCommentData>
}