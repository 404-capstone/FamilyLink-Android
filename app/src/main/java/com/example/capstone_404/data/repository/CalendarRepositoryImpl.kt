package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.CalendarApi
import com.example.capstone_404.data.retrofit.model.request.AddGroupScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddScheduleCommentRequest
import com.example.capstone_404.data.retrofit.model.request.EditScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.OptimizeRequest
import com.example.capstone_404.data.retrofit.model.response.AddGroupData
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.AddScheduleCommentData
import com.example.capstone_404.data.retrofit.model.response.EditScheduleData
import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import com.example.capstone_404.data.retrofit.model.response.ScheduleData
import com.example.capstone_404.data.retrofit.model.response.ScheduleDetailData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    private val calendarApi: CalendarApi
) : CalendarRepository {

    // 일정 전체 조회
    override suspend fun getAllSchedule(groupId: Int): Result<ScheduleData> {
        return try {
            val response = calendarApi.getAllSchedule(groupId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 개인 일정 추가
    override suspend fun addPersonalSchedule(body: AddPersonalScheduleRequest): Result<AddPersonalData> {
        return try {
            val response = calendarApi.addPersonalSchedule(body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 일정 최적화
    override suspend fun optimize(body: OptimizeRequest): Result<OptimizeData> {
        return try {
            val response = calendarApi.optimize(body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 가족 일정 추가
    override suspend fun addGroupSchedule(body: AddGroupScheduleRequest): Result<AddGroupData> {
        return try {
            val response = calendarApi.addGroupSchedule(body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 일정 수정
    override suspend fun editSchedule(body: EditScheduleRequest): Result<EditScheduleData> {
        return try {
            val response = calendarApi.editSchedule(body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 일정 상세 조회
    override suspend fun getScheduleDetail(scheduleId: Int): Result<ScheduleDetailData> {
        return try {
            val response = calendarApi.getScheduleDetail(scheduleId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 일정 댓글 작성
    override suspend fun addScheduleComment(body: AddScheduleCommentRequest): Result<AddScheduleCommentData> {
        return try {
            val response = calendarApi.addScheduleComment(body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 일정 삭제
    override suspend fun deleteSchedule(scheduleId: Int): Result<Int> {
        return try {
            val response = calendarApi.deleteSchedule(scheduleId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드 : ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}