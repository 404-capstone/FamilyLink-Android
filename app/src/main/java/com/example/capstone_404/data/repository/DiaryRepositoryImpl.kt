package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.retrofit.api.DiaryApi
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
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
                Log.e("DiaryRepository", "Error response body: $errorBody")
                Result.failure(Exception("오류 코드: ${response.code()}"))
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
                Log.e("DiaryRepository", "Error response body: $errorBody")
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}