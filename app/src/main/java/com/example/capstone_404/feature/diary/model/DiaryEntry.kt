package com.example.capstone_404.feature.diary.model

// 다이어리 엔트리 모델
data class DiaryEntry(
    val id: String,
    val date: String,
    val emotion: EmotionType           // 상위 감정 1개
)