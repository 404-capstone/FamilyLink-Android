package com.example.capstone_404

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

// HiltViewModel 쓸 경우 @HiltAndroidApp Application 클래스 있어야 됨
@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    // FCM 알림 채널 생성
    private fun createNotificationChannels() {
        val generalChannel = NotificationChannel(
            CHANNEL_ID_GENERAL,
            "일반 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "가족 그룹 관련 모든 알림"
            enableLights(true)
            enableVibration(true)
        }

        // 추후 도메인별 채널 분리 시 여기에 추가

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(generalChannel)
    }

    companion object {
        const val CHANNEL_ID_GENERAL = "general"
        // 추후 도메인별 채널 추가 시 여기에 상수 추가
    }
}