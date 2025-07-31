package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// Response<BaseResponse<T>>로 통일
@Serializable
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)