package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.KakaoLoginData

interface AuthRepository {
    suspend fun loginWithKakao(): Result<KakaoLoginData>
}