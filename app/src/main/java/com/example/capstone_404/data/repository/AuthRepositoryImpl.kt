package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.retrofit.api.AuthApi
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.token.TokenManager
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @Named("auth_no_token") private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    // SessionId로 토큰 발급
    override suspend fun loginWithSession(sessionId: String): Result<SocialLoginData> {
        return try {
            val response = authApi.loginWithSession(sessionId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                Result.failure(Exception("로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 수동 토큰 재발급
    override suspend fun tokenRefresh(providedRefreshToken: String): Result<TokenData> {
        return try {
            val response = authApi.refreshAccessToken("Bearer $providedRefreshToken")

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    tokenManager.saveTokens(data.accessToken, data.refreshToken)
                    Log.d("TokenTest", "AccessToken: ${data.accessToken}")
                    Log.d("TokenTest", "RefreshToken: ${data.refreshToken}")
                    Result.success(data)
                } ?: Result.failure(Exception("토큰이 비어있습니다."))
            } else {
                Result.failure(Exception("토큰 재발급 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}