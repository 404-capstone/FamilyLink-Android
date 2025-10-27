package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.retrofit.api.AlarmApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepositoryImpl @Inject constructor(
    private val alarmApi: AlarmApi
) : AlarmRepository {

    // FCM 토큰 갱신
    override suspend fun refreshFcmToken(androidToken: String): Result<Unit> {
        return try {
            val response = alarmApi.refreshFcmToken(androidToken)

            if (response.isSuccessful) {
                val message = response.body()?.data ?: "응답 없음"
                Log.d("AlarmRepository", "FCM 토큰 갱신 성공: $message")
                Result.success(Unit)
            } else {
                Log.e("AlarmRepository", "FCM 토큰 갱신 실패: ${response.code()}")
                Result.failure(Exception("FCM 토큰 갱신 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AlarmRepository", "FCM 토큰 갱신 중 오류: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 알림 수신 동의 설정
    override suspend fun setAlarmSetting(flag: Boolean): Result<Unit> {
        return try {
            val response = alarmApi.setAlarmSetting(flag)

            if (response.isSuccessful) {
                val message = response.body()?.data ?: "응답 없음"
                Log.d("AlarmRepository", "알림 설정 변경 성공: flag: $flag, message: $message")
                Result.success(Unit)
            } else {
                Log.e("AlarmRepository", "알림 설정 변경 실패: ${response.code()}")
                Result.failure(Exception("알림 설정 변경 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("AlarmRepository", "알림 설정 변경 중 오류: ${e.message}")
            Result.failure(e)
        }
    }
}