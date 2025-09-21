package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.request.DiaryCreateRequest
import com.example.capstone_404.data.retrofit.model.request.QuestionAnswerRequest
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.data.retrofit.model.response.FeedBackData
import com.example.capstone_404.data.retrofit.model.response.GroupAnswerDetailData
import com.example.capstone_404.data.retrofit.model.response.TodayQuestionData

// 다이어리 API Repository 인터페이스
interface DiaryRepository {

    // 일기, 그룹 공통질문 전체 조회
    suspend fun getDiaryAndQuestions(
        userId: Int
    ): Result<DiaryAllSearchData>

    // 일기 상세 조회
    suspend fun getDiaryDetail(
        diaryId: Int
    ): Result<DiaryDetailData>

    // 일기 삭제
    suspend fun deleteDiary(
        diaryId: Int
    ): Result<String>

    // 다이어리 작성 체크
    suspend fun checkDiaryWritable(
        groupId: Int
    ): Result<String>

    // 질문지 조회
    suspend fun getTodayQuestions(
        groupId: Int
    ): Result<TodayQuestionData>

    // 다이어리 작성
    suspend fun createDiary(
        request: DiaryCreateRequest
    ): Result<FeedBackData>

    // 질문 응답 저장
    suspend fun saveQuestionAnswers(
        request: QuestionAnswerRequest
    ): Result<String>

    // 공통질문 상세 조회
    suspend fun getQuestionAnswerDetail(
        groupId: Int, groupQuestionId: Int
    ): Result<GroupAnswerDetailData>
}