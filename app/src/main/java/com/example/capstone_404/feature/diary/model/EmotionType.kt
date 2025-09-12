package com.example.capstone_404.feature.diary.model

import androidx.compose.ui.graphics.Color

// 감정 타입 정의
enum class EmotionType {
    HAPPINESS, DISGUST, SURPRISE, SADNESS, ANGER, ANXIETY
}

// 감정별 색상 매핑 상수
object EmotionColorConstants {
    val HAPPINESS = Color(0xFFF6C945)
    val DISGUST = Color(0xFF7E8C13)
    val SURPRISE = Color(0xFFF2A77A)
    val SADNESS = Color(0xFF2471A3)
    val ANGER = Color(0xFFCB2874)
    val ANXIETY = Color(0xFF9E9E9E)
}

// 감정 분석 결과
data class EmotionResult(
    val emotion: String,
    val percentage: Float,
    val color: Color
)