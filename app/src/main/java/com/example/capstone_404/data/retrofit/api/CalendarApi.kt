package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.ScheduleData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
}