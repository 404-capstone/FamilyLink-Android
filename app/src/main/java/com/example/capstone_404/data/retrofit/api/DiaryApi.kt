package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.request.DiaryCreateRequest
import com.example.capstone_404.data.retrofit.model.request.QuestionAnswerRequest
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.data.retrofit.model.response.FeedBackData
import com.example.capstone_404.data.retrofit.model.response.GroupAnswerDetailData
import com.example.capstone_404.data.retrofit.model.response.TodayQuestionData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// 다이어리 관련 API 인터페이스
interface DiaryApi {

    // 다이어리 전체 조회
    @GET("/diary/all/search")
    suspend fun getDiaryAndQuestions(
        @Query("userId") userId: Int
    ): Response<BaseResponse<DiaryAllSearchData>>

    // 일기 상세 조회
    @GET("/diary/search")
    suspend fun getDiaryDetail(
        @Query("diaryId") diaryId: Int
    ): Response<BaseResponse<DiaryDetailData>>

    // 일기 삭제
    @DELETE("/diary/delete")
    suspend fun deleteDiary(
        @Query("diaryId") diaryId: Int
    ): Response<BaseResponse<String>>

    // 질문지 조회
    @GET("/diary/question/search")
    suspend fun getTodayQuestions(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<TodayQuestionData>>

    // 다이어리 작성 (AI 피드백 생성)
    @POST("/diary/write")
    suspend fun createDiary(
        @Body body: DiaryCreateRequest
    ): Response<BaseResponse<FeedBackData>>

    // 질문 응답 저장
    @POST("/diary/question/write")
    suspend fun saveQuestionAnswers(
        @Body body: QuestionAnswerRequest
    ): Response<BaseResponse<String>>

    // 공통질문 상세 조회
    @GET("/diary/question/answer/search")
    suspend fun getQuestionAnswerDetail(
        @Query("groupId") groupId: Int,
        @Query("groupQuestionId") groupQuestionId: Int
    ): Response<BaseResponse<GroupAnswerDetailData>>

    // 다이어리 작성 체크
    @GET("/diary/diary/check")
    suspend fun checkDiaryWritable(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<String>>
}