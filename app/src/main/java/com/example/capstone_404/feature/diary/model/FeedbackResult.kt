package com.example.capstone_404.feature.diary.model

// 피드백 결과 (임시 모델 - API 연동 시 교체)
data class FeedbackResult(
    val diaryText: String,
    val emotionResults: List<EmotionResult>,
    val aiFeedback: String
)