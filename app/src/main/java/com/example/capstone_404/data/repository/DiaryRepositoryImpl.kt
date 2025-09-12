package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.retrofit.api.DiaryApi
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.data.retrofit.model.response.TodayQuestionData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val diaryApi: DiaryApi
) : DiaryRepository {

    override suspend fun getDiaryAndQuestions(userId: Int): Result<DiaryAllSearchData> {
        return try {
            val response = diaryApi.getDiaryAndQuestions(userId)

            if (response.isSuccessful) {
                val data = response.body()?.data ?: DiaryAllSearchData(
                    diary = emptyList(),
                    questions = emptyList()
                )
                Result.success(data)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "다이어리 전체 조회 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("다이어리 정보를 불러오는데 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDiaryDetail(diaryId: Long): Result<DiaryDetailData> {
        return try {
            val response = diaryApi.getDiaryDetail(diaryId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "다이어리 상세 조회 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("다이어리 정보를 불러오는데 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDiary(diaryId: Long): Result<String> {
        return try {
            val response = diaryApi.deleteDiary(diaryId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.success("삭제 완료")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "다이어리 삭제 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("다이어리 삭제에 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "Delete exception: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getTodayQuestions(groupId: Int): Result<TodayQuestionData> {
        return try {
            val response = diaryApi.getTodayQuestions(groupId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                if (response.code() == 500) {
                    Log.w("DiaryRepository", "이미 응답 완료: $errorBody")
                    Result.failure(Exception("오늘 해당 응답을 하셨습니다."))
                } else {
                    Log.e("DiaryRepository", "질문지 조회 API 에러 [${response.code()}]: $errorBody")
                    Result.failure(Exception("질문을 불러오는데 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
                }
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "질문지 조회 실패: ${e.message}")
            Result.failure(e)
        }
    }
}