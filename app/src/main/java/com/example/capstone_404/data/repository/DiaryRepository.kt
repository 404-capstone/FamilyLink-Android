package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData

// 다이어리 API Repository 인터페이스
interface DiaryRepository {

    // 일기, 그룹 공통질문 전체 조회
    suspend fun getDiaryAndQuestions(
        userId: Int
    ): Result<DiaryAllSearchData>

    // 일기 상세 조회
    suspend fun getDiaryDetail(
        diaryId: Long
    ): Result<DiaryDetailData>
}