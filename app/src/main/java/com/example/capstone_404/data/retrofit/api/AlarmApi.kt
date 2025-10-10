package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Response

// 알림 관련 API 인터페이스
interface AlarmApi {

    // FCM 토큰 갱신
    @POST("/alarm/renewal")
    suspend fun refreshFcmToken(
        @Query("androidToken") androidToken: String
    ): Response<BaseResponse<String>>
}