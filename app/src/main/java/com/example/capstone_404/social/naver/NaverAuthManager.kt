package com.example.capstone_404.social.naver

import android.app.Activity
import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NaverAuthManager {
    suspend fun login(context: Context): String = suspendCoroutine { continuation ->
        val activity = context as? Activity
            ?: return@suspendCoroutine continuation.resumeWithException(
                IllegalStateException("네이버 로그인 중 에러 발생")    // context != Activity 일 때 발생
            )

        NaverIdLoginSDK.authenticate(activity, object : OAuthLoginCallback {
            override fun onSuccess() {
                val token = NaverIdLoginSDK.getAccessToken()
                if (!token.isNullOrEmpty()) {
                    continuation.resume(token)
                } else {
                    continuation.resumeWithException(Exception("토큰 없음"))
                }
            }
            override fun onFailure(httpStatus: Int, message: String) {
                continuation.resumeWithException(Exception(message))
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }
}