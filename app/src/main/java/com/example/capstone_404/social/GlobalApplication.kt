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

        // Naver SDK 초기화
        val naverId = BuildConfig.NAVER_CLIENT_ID
        val naverSecret = BuildConfig.NAVER_CLIENT_SECRET
        NaverIdLoginSDK.initialize(
            this,
            naverId,
            naverSecret,
            "Family Link"
        )
    }
}