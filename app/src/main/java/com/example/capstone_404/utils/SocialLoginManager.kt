package com.example.capstone_404.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.capstone_404.BuildConfig

// 네이버 & 카카오 통합 매니저
object SocialLoginManager {

    private const val BASE_URL = BuildConfig.BASE_URL

    fun login(context: Context, platform: SocialPlatform) {
        val loginUrl = when (platform) {
            SocialPlatform.NAVER -> "$BASE_URL/oauth2/authorization/naver"
            SocialPlatform.KAKAO -> "$BASE_URL/oauth2/authorization/kakao"
        }

        val activity = context as? Activity ?: return
        val uri = loginUrl.toUri()
        activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    enum class SocialPlatform { NAVER, KAKAO }
}