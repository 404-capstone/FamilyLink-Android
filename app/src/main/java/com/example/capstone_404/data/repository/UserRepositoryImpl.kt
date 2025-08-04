package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.retrofit.api.UserApi
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.UserInfoData
import com.example.capstone_404.data.retrofit.token.TokenManager
import com.example.capstone_404.utils.AgeConverter
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @Named("auth_no_token") private val noTokenUserApi: UserApi,
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
) : UserRepository {

    // SessionId로 토큰 발급
    override suspend fun loginWithSession(sessionId: String): Result<SocialLoginData> {
        return try {
            val response = noTokenUserApi.loginWithSession(sessionId)

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
            val response = noTokenUserApi.refreshAccessToken("Bearer $providedRefreshToken")

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
    override suspend fun getUserInfo(): Result<UserInfoData> {
        return try {
            val response = userApi.getUserInfo()

            if (response.isSuccessful) {
                val baseResponse = response.body()
                if (baseResponse?.data != null) {
                    // 사용자 정보 로컬 저장
                    val userInfo = baseResponse.data
                    userInfoManager.saveNickname(userInfo.username)

                    // 성별 저장
                    userInfoManager.saveGender(userInfo.gender)

                    // 나이 - 연령대 변환 저장
                    val ageRange = AgeConverter.convertAgeToRange(userInfo.age)
                    if (ageRange != null) {
                        userInfoManager.saveAge(ageRange)
                    }

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
}