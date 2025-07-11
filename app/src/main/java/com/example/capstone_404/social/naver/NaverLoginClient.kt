package com.example.capstone_404.social.naver

import android.app.Activity
import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

//네이버 로그인 요청 및 토큰 반환을 담당
object NaverLoginClient {
    suspend fun login(context: Context): String = suspendCoroutine { continuation ->
        val activity = context as? Activity
            ?: return@suspendCoroutine continuation.resumeWithException(
                IllegalStateException("네이버 로그인 중 에러 발생")   //네이버 로그인은 Activity에서만 가능
            )

        val callback = object : OAuthLoginCallback {
            override fun onSuccess() {
                val token = NaverIdLoginSDK.getAccessToken()
                if (token != null) continuation.resume(token)
                else continuation.resumeWithException(Exception("토큰 없음"))
            }
            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resumeWithException(Exception("네이버 로그인 실패: $message"))
            }
            override fun onError(errorCode: Int, message: String) {
                continuation.resumeWithException(Exception("네이버 로그인 에러: $message"))
            }
        }
        NaverIdLoginSDK.authenticate(activity, callback)
    }
}