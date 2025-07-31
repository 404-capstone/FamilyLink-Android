package com.example.capstone_404.data.retrofit.model.response

// 소셜 로그인 : 토큰 반환
data class SocialLoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val flag: Boolean
)