package com.example.capstone_404.data.retrofit.model.request

// 설문 결과 저장 Request
data class SaveSurveyResultRequest(
    val level: String,
    val score: Int,
    val percent: Int
)