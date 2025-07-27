package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.KakaoLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.TokenRefreshResponse

interface AuthRepository {
    suspend fun loginWithKakao(): Result<KakaoLoginData>

    // 수동 토큰 재발급
    suspend fun tokenRefresh(providedRefreshToken: String): Result<TokenData>
}