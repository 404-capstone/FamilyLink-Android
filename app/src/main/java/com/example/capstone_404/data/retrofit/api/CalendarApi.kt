package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.request.AddGroupScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddScheduleCommentRequest
import com.example.capstone_404.data.retrofit.model.request.EditScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.OptimizeRequest
import com.example.capstone_404.data.retrofit.model.response.AddGroupData
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.AddScheduleCommentData
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.EditScheduleData
import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import com.example.capstone_404.data.retrofit.model.response.ScheduleData
import com.example.capstone_404.data.retrofit.model.response.ScheduleDetailData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

// 캘린더 관련 API 인터페이스
interface CalendarApi {

    // 일정 전체 조회
    @GET("/schedule/all/search")
    suspend fun getAllSchedule(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<ScheduleData>>

    // 개인 일정 추가
    @POST("/schedule/user/add")
    suspend fun addPersonalSchedule(
        @Body body: AddPersonalScheduleRequest
    ): Response<BaseResponse<AddPersonalData>>

    // 일정 최적화
    @POST("/schedule/optimal")
    suspend fun optimize(
        @Body body: OptimizeRequest
    ): Response<BaseResponse<OptimizeData>>

    // 가족 일정 추가
    @POST("/schedule/group/add")
    suspend fun addGroupSchedule(
        @Body body: AddGroupScheduleRequest
    ): Response<BaseResponse<AddGroupData>>

    // 일정 수정
    @PATCH("/schedule/edit")
    suspend fun editSchedule(
        @Body body: EditScheduleRequest
    ): Response<BaseResponse<EditScheduleData>>

    // 일정 상세 조회
    @GET("/schedule/comment")
    suspend fun getScheduleDetail(
        @Query("scheduleId") scheduleId: Int
    ): Response<BaseResponse<ScheduleDetailData>>

    // 일정 댓글 작성
    @POST("/schedule/comment/add")
    suspend fun addScheduleComment(
        @Body body: AddScheduleCommentRequest
    ): Response<BaseResponse<AddScheduleCommentData>>

    // 일정 삭제
    @DELETE("/schedule/delete")
    suspend fun deleteSchedule(
        @Query("scheduleId") scheduleId: Int
    ): Response<BaseResponse<Int>>
}