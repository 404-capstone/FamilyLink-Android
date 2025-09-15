package com.example.capstone_404.session

import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.retrofit.token.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager
) {
    private val _logoutEvents = MutableSharedFlow<LogoutReason>(
        replay = 0, extraBufferCapacity = 1
    )
    val logoutEvents: SharedFlow<LogoutReason> = _logoutEvents
    // 강제 로그아웃
    suspend fun forceLogout() {
        // 사용자 정보 삭제
        tokenManager.clearToken()
        userInfoManager.clearAll()
        groupInfoManager.clearAll()
        // 로그아웃 이벤트 실행
        _logoutEvents.emit(LogoutReason.ExpiredRefresh)
    }
}