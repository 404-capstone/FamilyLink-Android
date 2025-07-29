package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData

interface AuthRepository {
    // SessionId로 토큰 발급
    suspend fun loginWithSession(sessionId: String): Result<SocialLoginData>

    // 수동 토큰 재발급
    suspend fun tokenRefresh(providedRefreshToken: String): Result<TokenData>
}