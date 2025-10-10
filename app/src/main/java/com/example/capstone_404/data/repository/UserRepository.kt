package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.request.UserInfoEditRequest
import com.example.capstone_404.data.retrofit.model.response.SocialLoginData
import com.example.capstone_404.data.retrofit.model.response.TokenData
import com.example.capstone_404.data.retrofit.model.response.UserInfoData
import com.example.capstone_404.data.retrofit.model.response.UserInfoEditData

// 유저 관련 API Repository 인터페이스
interface UserRepository {

    // SessionId로 토큰 발급
    suspend fun loginWithSession(
        sessionId: String,
        fcmToken: String? = null
    ): Result<SocialLoginData>

    // 수동 토큰 재발급
    suspend fun tokenRefresh(
        providedRefreshToken: String
    ): Result<TokenData>

    // 유저 정보 조회
    suspend fun getUserInfo(
    ): Result<UserInfoData>

    // 로그아웃
    suspend fun logout(
    ): Result<Unit>

    // 회원 탈퇴
    suspend fun deleteUser(
    ): Result<Unit>

    // 프로필 변경
    suspend fun editUserInfo(
        request: UserInfoEditRequest
    ): Result<UserInfoEditData>
}