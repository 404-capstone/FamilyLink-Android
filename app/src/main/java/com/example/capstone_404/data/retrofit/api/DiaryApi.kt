package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import retrofit2.Response
import retrofit2.http.GET
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
        @Query("diaryId") diaryId: Long
    ): Response<BaseResponse<DiaryDetailData>>
}