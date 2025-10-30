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
    val diId: Int,
    val date: String,
    val emotion: String
)

// 그룹 공통질문 데이터 Response
@Serializable
data class GroupQuestionData(
    val gqId: Int,
    val date: String,
    val responders: List<String>
)

// 일기 상세 조회 Response
@Serializable
data class DiaryDetailData(
    val id: Int,
    val content: String,
    val userId: Int,
    val diaryAt: String,
    val feedBack: String,
    val emotions: List<EmotionData>,
    val familyEmotion: List<FamilyEmotionData> = emptyList()
)

// 가족 감정 데이터 Response
@Serializable
data class FamilyEmotionData(
    val userId: Int,
    val emotion: String
)

// 감정 분석 데이터 Response
@Serializable
data class EmotionData(
    val id: Int,
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

// 다이어리 작성(AI피드백 생성) Response
@Serializable
data class FeedBackData(
    val diary: String,
    val feedback: String,
    val emotions: List<EmotionAnalysisData>
)

@Serializable
data class EmotionAnalysisData(
    val emotion: String,
    val percent: Double
)

// 공통질문 상세 조회 Response
@Serializable
data class GroupAnswerDetailData(
    val groupQuestionId: Int,
    val date: String,
    val questionInfo: List<QuestionInfoData>
)

@Serializable
data class QuestionInfoData(
    val questionId: Int,
    val question: String,
    val answerInfo: List<AnswerInfoData>
)

@Serializable
data class AnswerInfoData(
    val userId: Int,
    val name: String,
    val postion: String,
    val answer: String?
)