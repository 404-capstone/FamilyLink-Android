package com.example.capstone_404.data.retrofit.model.request

// 사진 수정 Request Body
data class PhotoEditRequest(
    val photoId: Int,
    val title: String,
    val area: String?,
    val content: String?,
    val date: String,
    val time: String?,
    val userId: List<Int>?
)