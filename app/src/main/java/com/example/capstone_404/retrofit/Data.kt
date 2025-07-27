package com.example.capstone_404.retrofit

data class JwtTokenResponse(
    val code: Int,
    val message: String,
    val data: UserLoginData
)

data class UserLoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val flag: Boolean
)