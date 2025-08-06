package com.example.capstone_404.data.retrofit.model.request

// 프로필 변경 Request
data class UserInfoEditRequest(
    val username: String,
    val age: Int,
    val gender: String,
    val image: String?
)