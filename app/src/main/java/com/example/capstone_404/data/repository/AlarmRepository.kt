package com.example.capstone_404.data.repository

// 알림 관련 Repository 인터페이스
interface AlarmRepository {

    // FCM 토큰 재갱신
    suspend fun refreshFcmToken(
        androidToken: String
    ): Result<Unit>

    // 알림 수신 동의 설정
    suspend fun setAlarmSetting(
        flag: Boolean
    ): Result<Unit>
}