package com.example.capstone_404.data.repository

import android.util.Log
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.retrofit.api.UserApi
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.UserInfoData
import com.example.capstone_404.data.retrofit.model.response.UserInfoEditData
import com.example.capstone_404.data.retrofit.token.TokenManager
import com.example.capstone_404.utils.AgeConverter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    @Named("auth_no_token") private val noTokenUserApi: UserApi,
    private val userApi: UserApi,
    private val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager
) : UserRepository {

    // SessionId로 토큰 발급
    override suspend fun loginWithSession(sessionId: String, fcmToken: String?): Result<SocialLoginData> {
        return try {
            Log.d("UserRepository", "로그인 시도 - sessionId: ${sessionId.take(10)}..., fcmToken: ${fcmToken?.take(10)}...")
            val response = noTokenUserApi.loginWithSession(sessionId, fcmToken)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    // 토큰 저장
                    tokenManager.saveTokens(data.accessToken, data.refreshToken)
                    // userId 저장
                    userInfoManager.saveUserId(data.userId)
                    Log.d("User_Info", "(L)userId 저장 완료 : ${data.userId}")
                    // sessionId 삭제
                    tokenManager.clearSessionId()
                    Log.d("User_Info", "(L)SessionId 삭제 완료")
                    Result.success(data)
                } ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                tokenManager.clearSessionId()
                Result.failure(Exception("로그인 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            tokenManager.clearSessionId()
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
                    Result.success(data)
                } ?: Result.failure(Exception("토큰이 비어 있음"))
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

                    // 알림 설정 저장
                    userInfoManager.saveAlarmEnabled(userInfo.alarm)
                    Log.d("AuthRepository", "알림 설정 저장 성공: ${userInfo.alarm}")

                    //정보 조회 확인 log
                    Log.d("AuthRepository", "사용자 정보 조회 성공: ${userInfo.username}")
                    Log.d("AuthRepository", "사용자 정보 조회 성공: ${userInfo.gender}")
                    Log.d("AuthRepository", "사용자 정보 조회 성공: $ageRange")
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

    // 로그아웃
    override suspend fun logout(): Result<Unit> {
        return try {
            val response = userApi.logout()

            if (response.isSuccessful) {
                // 모든 로컬 데이터 삭제
                tokenManager.clearToken()
                userInfoManager.clearAll()
                groupInfoManager.clearAll()
                Log.d("AuthRepository", "로그아웃 성공")
                Result.success(Unit)
            } else {
                val errorMsg = "로그아웃 실패: ${response.code()}"
                Log.e("AuthRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "로그아웃 실패: ${e.message}")
            Result.failure(e)
        }
    }

    // 회원 탈퇴
    override suspend fun deleteUser(): Result<Unit> {
        return try {
            val response = userApi.deleteUser()

            if (response.isSuccessful) {
                // 모든 로컬 데이터 삭제
                tokenManager.clearToken()
                userInfoManager.clearAll()
                groupInfoManager.clearAll()
                Log.d("AuthRepository", "회원 탈퇴 성공")
                Result.success(Unit)
            } else {
                val errorMsg = "회원 탈퇴 실패: ${response.code()}"
                Log.e("AuthRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "회원 탈퇴 실패: ${e.message}")
            Result.failure(e)
        }
    }

    // 프로필 변경
    override suspend fun editUserInfo(
        username: String,
        age: Int,
        gender: String,
        imageFile: MultipartBody.Part
    ): Result<UserInfoEditData> {
        return try {
            val usernamePart = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val agePart = age.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = userApi.editUserInfo(usernamePart, agePart, genderPart, imageFile)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    // 로컬에 변경된 정보 저장
                    userInfoManager.saveNickname(data.username)
                    userInfoManager.saveGender(data.gender)

                    // 나이 - 연령대 변환 저장
                    val ageRange = AgeConverter.convertAgeToRange(data.age)
                    if (ageRange != null) {
                        userInfoManager.saveAge(ageRange)
                    }

                    //프로필 변경 확인 log
                    Log.d("AuthRepository", "프로필 변경 성공: ${data.username}")
                    Log.d("AuthRepository", "프로필 변경 성공: ${data.gender}")
                    Log.d("AuthRepository", "프로필 변경 성공: $ageRange")
                    Result.success(data)
                } ?: Result.failure(Exception("응답 본문이 비어 있음"))
            } else {
                val errorMsg = "프로필 변경 실패: ${response.code()}"
                Log.e("AuthRepository", errorMsg)
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "프로필 변경 실패: ${e.message}")
            Result.failure(e)
        }
    }
}