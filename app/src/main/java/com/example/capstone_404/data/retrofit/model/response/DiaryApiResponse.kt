package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 다이어리 전체 조회 Response
@Serializable
data class DiaryAllSearchData(
    val diary: List<DiaryData>,
    val questions: List<GroupQuestionData>
)

// 일기 데이터 Response
@Serializable
data class DiaryData(
    val diId: Long,
    val date: String,
    val emotion: String
)

// 그룹 공통질문 데이터 Response
@Serializable
data class GroupQuestionData(
    val gqId: Long,
    val date: String,
    val responders: List<String>
)

// 일기 상세 조회 Response
@Serializable
data class DiaryDetailData(
    val id: Long,
    val content: String,
    val userId: Long,
    val diaryAt: String,
    val feedBack: String,
    val emotions: List<EmotionData>
)

// 감정 분석 데이터 Response
@Serializable
data class EmotionData(
    val id: Long,
    val label: String,
    val score: Double
)

// 질문지 조회 Response
@Serializable
data class TodayQuestionData(
    val groupId: Int,
    val groupQuestion: List<GroupQuestionItem>
)

@Serializable
data class GroupQuestionItem(
    val questionId: Int,
    val content: String
)