package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val username: String,
    val gender: String,
    val age: Int,
    val image: String?,
    val social: String
)