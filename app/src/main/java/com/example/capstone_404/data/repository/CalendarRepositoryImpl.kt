package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.CalendarApi
import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.response.AddPersonalData
import com.example.capstone_404.data.retrofit.model.response.ScheduleData
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
}