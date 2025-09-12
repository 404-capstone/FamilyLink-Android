package com.example.capstone_404.data.retrofit.model.request

import kotlinx.serialization.Serializable

// 다이어리 작성 Request
@Serializable
data class DiaryCreateRequest(
    val content: String,
    val userId: Long
)