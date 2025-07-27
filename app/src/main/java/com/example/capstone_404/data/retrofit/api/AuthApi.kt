package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.KakaoLoginResponse
import com.example.capstone_404.data.retrofit.model.response.TokenRefreshResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

// 로그인 관련 API 인터페이스
interface AuthApi {

    // 카카오 소셜 로그인
    @GET("/user/custom/kakao")
    suspend fun loginWithKakao(): Response<KakaoLoginResponse>

    // 토큰 재발급 (만료 시 자동)
    @GET("/user/token/refresh")
    suspend fun refreshAccessToken(
        @Header("Refresh-Token") refreshToken: String
    ): Response<TokenRefreshResponse>
}