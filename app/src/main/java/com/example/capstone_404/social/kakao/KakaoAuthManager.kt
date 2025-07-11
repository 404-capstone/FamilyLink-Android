package com.example.capstone_404.social.kakao

import android.app.Activity
import android.content.Context
import com.kakao.sdk.user.UserApiClient
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object KakaoAuthManager {
    suspend fun login(context: Context): String = suspendCoroutine { continuation ->
        val activity = context as? Activity
            ?: return@suspendCoroutine continuation.resumeWithException(
                IllegalStateException("카카오 로그인 중 에러 발생")    // context != Activity 일 때 발생
            )

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(activity) { token, error ->
                if (token != null) continuation.resume(token.accessToken)
                else continuation.resumeWithException(error ?: Exception("카카오톡 로그인 실패"))
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(activity) { token, error ->
                if (token != null) continuation.resume(token.accessToken)
                else continuation.resumeWithException(error ?: Exception("카카오 계정 로그인 실패"))
            }
        }
    }
}