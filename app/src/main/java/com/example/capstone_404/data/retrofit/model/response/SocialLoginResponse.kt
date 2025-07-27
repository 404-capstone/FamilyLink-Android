package com.example.capstone_404.data.retrofit.model.response

data class KakaoLoginResponse(
    val code: Int,
    val message: String,
    val data: KakaoLoginData
)

data class KakaoLoginData(
    val id: Long,
    val social: String,
    val newUser: Boolean,
    val accessToken: String,
    val refreshToken: String
)