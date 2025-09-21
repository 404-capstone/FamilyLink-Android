package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.retrofit.api.DiaryApi
import com.example.capstone_404.data.retrofit.model.request.DiaryCreateRequest
import com.example.capstone_404.data.retrofit.model.request.QuestionAnswerRequest
import com.example.capstone_404.data.retrofit.model.response.DiaryAllSearchData
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.data.retrofit.model.response.FeedBackData
import com.example.capstone_404.data.retrofit.model.response.GroupAnswerDetailData
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

    override suspend fun getDiaryDetail(diaryId: Int): Result<DiaryDetailData> {
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

    override suspend fun deleteDiary(diaryId: Int): Result<String> {
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
                Log.e("DiaryRepository", "질문지 조회 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("질문을 불러오는데 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "질문지 조회 실패: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun checkDiaryWritable(groupId: Int): Result<String> {
        return try {
            val response = diaryApi.checkDiaryWritable(groupId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                if (response.code() == 500) {
                    // 이미 다이어리 작성한 경우
                    Log.w("DiaryRepository", "다이어리 이미 작성됨: $errorBody")
                    Result.failure(Exception("다이어리가 존재하여 작성할수 없습니다."))
                } else {
                    Log.e("DiaryRepository", "다이어리 체크 에러 [${response.code()}]: $errorBody")
                    Result.failure(Exception("다이어리 작성 체크에 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
                }
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "다이어리 체크 실패: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun createDiary(request: DiaryCreateRequest): Result<FeedBackData> {
        return try {
            val response = diaryApi.createDiary(request)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Log.d("DiaryRepository", "다이어리 작성 성공: $data")
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "다이어리 작성 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("다이어리 저장에 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "다이어리 작성 실패: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun saveQuestionAnswers(request: QuestionAnswerRequest): Result<String> {
        return try {
            val response = diaryApi.saveQuestionAnswers(request)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Log.d("DiaryRepository", "질문 답변 저장 성공: $data")
                    Result.success(data)
                } ?: Result.success("질문 답변 저장 완료")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "질문 답변 저장 API 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("질문 답변 저장에 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "질문 답변 저장 실패: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getQuestionAnswerDetail(
        groupId: Int, groupQuestionId: Int
    ): Result<GroupAnswerDetailData> {
        return try {
            val response = diaryApi.getQuestionAnswerDetail(groupId, groupQuestionId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DiaryRepository", "공통질문 상세 조회 에러 [${response.code()}]: $errorBody")
                Result.failure(Exception("공통질문 정보를 불러오는데 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."))
            }
        } catch (e: Exception) {
            Log.e("DiaryRepository", "공통질문 상세 조회 실패: ${e.message}")
            Result.failure(e)
        }
    }
}