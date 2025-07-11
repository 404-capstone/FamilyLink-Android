package com.example.capstone_404.social

import android.app.Application
import com.example.capstone_404.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        val kakaoKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        KakaoSdk.init(this, kakaoKey)

        // 네이버 로그인 SDK 초기화
        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            "Family Link" // 앱 이름
        )
    }
}