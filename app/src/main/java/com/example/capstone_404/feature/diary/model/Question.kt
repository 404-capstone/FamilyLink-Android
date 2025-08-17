package com.example.capstone_404.feature.diary.model

import androidx.compose.ui.graphics.Color

// 공통 질문 모델
data class Question(
    val id: String,
    val date: String,
    val question: String,
    val responders: List<Responder> = emptyList() // 해당 질문에 응답한 그룹원 목록
)

// 질문 응답자 모델
data class Responder(
    val roleLabel: String,              // 그룹 역할
    val color: Color                    // 그룹 역할 색상
)