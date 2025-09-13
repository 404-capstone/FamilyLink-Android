package com.example.capstone_404.data.retrofit.model.request

import kotlinx.serialization.Serializable

// 다이어리 작성 Request
@Serializable
data class DiaryCreateRequest(
    val content: String,
    val userId: Int
)

// 질문 응답 저장 Request
@Serializable
data class QuestionAnswerRequest(
    val groupId: Int,
    val questions: List<QuestionAnswer>
)

@Serializable
data class QuestionAnswer(
    val questionId: Int,
    val content: String?
)