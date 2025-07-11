package com.example.capstone_404.social

import android.app.Application
import com.example.capstone_404.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        val kakaoKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        KakaoSdk.init(this, kakaoKey)
    }
}