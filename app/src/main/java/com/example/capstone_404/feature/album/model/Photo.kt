package com.example.capstone_404.feature.album.model

// UI용 Photo 모델
data class Photo(
    val id: Int,
    val title: String,
    val thumbnailUrl: String,
    val content: String,
    val area: String,
    val time: Long,           // 타임스탬프
    val userIds: List<Int>    // 태그된 사용자 ID 목록
)