package com.example.capstone_404.data.retrofit.model.response

// 토큰 재발급 response
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)