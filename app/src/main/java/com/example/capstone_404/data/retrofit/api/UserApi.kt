package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.request.UserInfoEditRequest
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.UserInfoData
import com.example.capstone_404.data.retrofit.model.response.UserInfoEditData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// 로그인 관련 API 인터페이스
interface UserApi {

    // SessionId로 토큰 발급
    @GET("/user/login/code")
    suspend fun loginWithSession(
        @Query("session") sessionId: String,
        @Query("fcmToken") fcmToken: String? = null
    ): Response<BaseResponse<SocialLoginData>>

    // 토큰 재발급 (만료 시 자동)
    @GET("/user/token/refresh")
    suspend fun refreshAccessToken(
        @Header("Refresh-Token") refreshToken: String
    ): Response<BaseResponse<TokenData>>

    // 사용자 정보 조회
    @GET("/user/search")
    suspend fun getUserInfo(
    ): Response<BaseResponse<UserInfoData>>

    // 로그아웃
    @POST("/user/logout")
    suspend fun logout(
    ): Response<Unit>

    // 회원 탈퇴
    @DELETE("/user/delete")
    suspend fun deleteUser(
    ): Response<Unit>

    // 프로필 변경
    @PUT("/user/info/edit")
    suspend fun editUserInfo(
        @Body request: UserInfoEditRequest
    ): Response<BaseResponse<UserInfoEditData>>
}