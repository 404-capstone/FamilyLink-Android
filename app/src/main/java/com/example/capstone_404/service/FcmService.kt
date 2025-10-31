package com.example.capstone_404.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.capstone_404.HiltApplication
import com.example.capstone_404.MainActivity
import com.example.capstone_404.R
import com.example.capstone_404.data.repository.AlarmRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 토큰 재갱신 시 서버로 전송
        serviceScope.launch {
            alarmRepository.refreshFcmToken(token)
                .onSuccess {
                    Log.d("FCM", "토큰 갱신 성공")
                }
                .onFailure { e ->
                    Log.e("FCM", "토큰 갱신 실패: ${e.message}")
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.data["title"]
        val bodyRaw = remoteMessage.data["data"]
        val type = remoteMessage.data["type"]

        Log.d("FCM", "메시지 수신: title=$title, body=$bodyRaw, type=$type")

        val body = parseMessageBody(bodyRaw)
        val ids = parseIdsFromBody(bodyRaw)
        val schId = ids["sch_id"]
        // Todo : 맞춰서 id 추가
        Log.d("FCM", "메시지 파싱 완료: title=$title, body=$body, ids=$ids, type=$type")

        if (title != null && body != null) {
            sendNotification(title, body, type, remoteMessage.data, schId)
        } else {
            Log.e("FCM", "필수 데이터 누락 - title: $title, body: $body, type=$type")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d("FCM", "FcmService 종료")
    }

    // body 메시지 파싱 (JSON 형식 처리)
    private fun parseMessageBody(bodyRaw: String?): String? {
        if (bodyRaw == null) return null

        return try {
            if (bodyRaw.trim().startsWith("{") && bodyRaw.contains("message")) {
                val regex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                val matchResult = regex.find(bodyRaw)
                matchResult?.groupValues?.getOrNull(1) ?: bodyRaw
            } else {
                bodyRaw
            }
        } catch (e: Exception) {
            Log.e("FCM", "메시지 파싱 실패: ${e.message}, 원본 반환")
            bodyRaw
        }
    }

    // body에서 특정 ID 파싱
    private fun parseIdsFromBody(bodyRaw: String?): Map<String, Int> {
        if (bodyRaw.isNullOrBlank()) return emptyMap()
        val idRegex = "\"([a-zA-Z_]+_id)\"\\s*:\\s*\"?(\\d+)\"?".toRegex()
        return idRegex.findAll(bodyRaw).mapNotNull { result ->
            val key = result.groupValues[1]
            val value = result.groupValues[2].toIntOrNull() ?: return@mapNotNull null
            key to value
        }.toMap()
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        type: String?,
        data: Map<String, String>,
        schId: Int? = null,
        // Todo : 맞춰서 id 추가
    ) {
        // Notification ID 생성 (오버플로우 방지)
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        // type별 딥링크
        val deepLink: String? = when (type) {
            // 캘린더
            "calendar-1", "calendar-2", "calendar-3" -> buildString {
                append("familylink://launch?type=").append(type)
                if (schId != null) append("&sch_id=").append(schId)
            }
            // Todo : 맞춰서 딥링크 추가
            else -> null
        }

        val intent = if (deepLink != null) {
            Intent(
                Intent.ACTION_VIEW,
                deepLink.toUri(),
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else {
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("notification_type", type)
                data.forEach { (key, value) ->
                    putExtra(key, value)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 채널 ID 결정 (추후 확장)
        val channelId = resolveChannelId(type)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d("FCM", "알림 표시 완료: ID=$notificationId, title=$title")
    }

    // 채널 결정 로직 (추후 도메인별 분리 시 확장)
    private fun resolveChannelId(type: String?): String {
        // 현재는 모든 알림이 general 채널 사용
        return HiltApplication.CHANNEL_ID_GENERAL
    }
}