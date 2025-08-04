package com.example.capstone_404.data.retrofit.token

import com.example.capstone_404.data.retrofit.api.UserApi
import com.example.capstone_404.utils.EncryptionUtil
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

// 토큰 만료 시 자동 재발급
class TokenAutoRefresh @Inject constructor(
    private val tokenManager: TokenManager,
    private val userApi: UserApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 무한 루프 방지
        if (responseCount(response) >= 3) return null

        // refreshToken 복호화
        val refreshToken = runBlocking {
            val encryptedRefresh = tokenManager.getEncryptedRefreshToken()
            EncryptionUtil.decrypt(encryptedRefresh)
        } ?: return null

        // refresh API 호출
        val tokenResponse = runBlocking {
            try {
                userApi.refreshAccessToken("Bearer $refreshToken")
            } catch (e: Exception) {
                null
            }
        } ?: return null

        if (!tokenResponse.isSuccessful || tokenResponse.body()?.data == null) {
            return null
        }

        val newAccessToken = tokenResponse.body()!!.data.accessToken
        val newRefreshToken = tokenResponse.body()!!.data.refreshToken

        // 새 토큰 저장
        runBlocking {
            tokenManager.saveTokens(newAccessToken, newRefreshToken)
        }

        // 만료로 실패했던 요청에 새로운 accessToken 추가해서 재시도
        return response.request.newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    // 무한 루프 방지용 요청 카운트
    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
