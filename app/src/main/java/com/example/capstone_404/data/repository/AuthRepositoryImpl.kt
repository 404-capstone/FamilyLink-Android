package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.retrofit.api.AuthApi
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.UserInfoResponse
import com.example.capstone_404.data.retrofit.token.TokenManager
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @Named("auth_no_token") private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
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

    // 사용자 정보 조회
    override suspend fun getUserInfo(): Result<UserInfoResponse> {
        return try {
            val accessToken = tokenManager.getAccessToken()
            if (accessToken.isBlank()) {
                return Result.failure(Exception("Access Token이 없습니다."))
            }

            val response = authApi.getUserInfo("Bearer $accessToken")

            if (response.isSuccessful) {
                val baseResponse = response.body()
                if (baseResponse != null && baseResponse.data != null) {
                    // 사용자 정보 로컬 저장
                    val userInfo = baseResponse.data
                    userInfoManager.saveNickname(userInfo.username)

                    // 성별 변환
                    val convertedGender = when(userInfo.gender) {
                        "남자" -> "남성"
                        "여자" -> "여성"
                        else -> userInfo.gender
                    }
                    userInfoManager.saveGender(convertedGender)

                    // 나이 - 연령대 변환 저장
                    val ageRange = convertAgeToRange(userInfo.age)
                    userInfoManager.saveAge(ageRange)

                    // 소셜 로그인 제공자 정보 저장
                    if (userInfo.social.isNotBlank()) {
                        userInfoManager.saveSocialProvider(userInfo.social)
                        Log.d("AuthRepository", "소셜 로그인 제공자 저장: ${userInfo.social}")
                    } else {
                        Log.w("AuthRepository", "소셜 로그인 제공자가 비어있음")
                    }

                    Log.d("AuthRepository", "사용자 정보 조회 성공: ${userInfo.username}")
                    Result.success(userInfo)
                } else {
                    val errorMsg = baseResponse?.message ?: "사용자 정보 조회 실패"
                    Log.e("AuthRepository", errorMsg)
                    Result.failure(Exception(errorMsg))
                }
            } else {
                val errorMsg = "서버 응답 오류: ${response.code()}"
                Log.e("AuthRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "사용자 정보 조회 실패: ${e.message}")
            Result.failure(e)
        }
    }

    // 나이 변환 함수
    private fun convertAgeToRange(age: Int): String {
        return when (age) {
            in 0..9 -> "10대 미만"
            in 10..19 -> "10대"
            in 20..29 -> "20대"
            in 30..39 -> "30대"
            in 40..49 -> "40대"
            in 50..59 -> "50대"
            in 60..69 -> "60대"
            in 70..79 -> "70대"
            in 80..89 -> "80대"
            else -> "90대 이상"
        }
    }
}