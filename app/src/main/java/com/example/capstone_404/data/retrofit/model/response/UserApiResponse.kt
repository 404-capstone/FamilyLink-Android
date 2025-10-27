package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 소셜 로그인 : 토큰 반환 Response
data class SocialLoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val flag: Boolean
)

// 토큰 재발급 response
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)

// 사용자 정보 조회 Response
@Serializable
data class UserInfoData(
    val username: String,
    val gender: String,
    val age: Int,
    val image: String?,
    val social: String,
    val alarm: Boolean
)

// 프로필 변경 Response
@Serializable
data class UserInfoEditData(
    val username: String,
    val age: Int,
    val gender: String,
    val image: String?
)