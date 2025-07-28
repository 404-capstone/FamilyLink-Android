package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.SocialLoginResponse
import com.example.capstone_404.data.retrofit.model.response.TokenRefreshResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// 로그인 관련 API 인터페이스
interface AuthApi {

    // SessionId로 토큰 발급
    @GET("/user/login/code")
    suspend fun loginWithSession(
        @Query("session") sessionId: String
    ): Response<SocialLoginResponse>

    // 토큰 재발급 (만료 시 자동)
    @GET("/user/token/refresh")
    suspend fun refreshAccessToken(
        @Header("Refresh-Token") refreshToken: String
    ): Response<TokenRefreshResponse>
}