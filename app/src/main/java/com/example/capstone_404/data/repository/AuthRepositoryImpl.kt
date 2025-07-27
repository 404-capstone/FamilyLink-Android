package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.AuthApi
import com.example.capstone_404.data.retrofit.model.response.KakaoLoginData
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @Named("auth_no_token") private val authApi: AuthApi
) : AuthRepository {

    override suspend fun loginWithKakao(): Result<KakaoLoginData> {
        return try {
            val response = authApi.loginWithKakao()
            if (response.isSuccessful) {
                response.body()?.data?.let { Result.success(it) }
                    ?: Result.failure(Exception("응답 본문이 비어 있습니다"))
            } else {
                Result.failure(Exception("로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}