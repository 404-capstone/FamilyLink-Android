package com.example.capstone_404.feature.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.AuthRepository
import com.example.capstone_404.data.repository.GroupRepository
import com.example.capstone_404.data.retrofit.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.capstone_404.data.info.GroupInfo
import com.example.capstone_404.data.info.GroupUserInfo

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
    val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager
) : ViewModel() {

    // 소셜 로그인 상태 관리
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // 자동 로그인용 상태 관리
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // 그룹 ID 저장 상태 관리
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    // 자동 로그인 체크 함수
    fun checkAutoLogin() {
        viewModelScope.launch {
            _isLoggedIn.value = tokenManager.hasValidToken()
        }
    }

    // DataStore에 SessionId 저장 함수
    fun saveSessionId(sessionId: String) {
        viewModelScope.launch {
            tokenManager.saveSessionId(sessionId)
        }
    }

    // DataStore에 groupID 저장 함수
    fun saveGroupId() {
        viewModelScope.launch {
            try {
                val result = groupRepository.getGroupIdFromServer()

                result.onSuccess { groupId ->
                    userInfoManager.saveGroupId(groupId)
                    Log.d("User_Info", "그룹 ID 저장 완료 : $groupId")
                    saveGroupInfo(groupId)
                }.onFailure { e ->
                    Log.d("User_Info", "그룹 ID 조회 실패 : ${e.message}")
                    _isSaved.value = true
                }
            } catch (e: Exception) {
                Log.e("User_Info", "그룹 ID 조회 실패 : ${e.message}")
                _isSaved.value = true
            }
        }
    }

    // DataStore에 그룹 정보 저장 함수
    private fun saveGroupInfo(groupId: Int) {
        viewModelScope.launch {
            val result = groupRepository.getGroupInfo(groupId)
            result.onSuccess { data ->
                val groupInfo = GroupInfo(
                    groupName = data.group_name,
                    groupImage = data.group_image ?: "",
                    userinfo = data.userinfo.map {
                        GroupUserInfo(
                            userId = it.userId,
                            username = it.username,
                            role = it.role,
                            age = it.age ?: "연령대 미지정",
                            image = it.image ?: "",
                            leader = it.leader
                        )
                    }
                )
                groupInfoManager.saveGroupInfo(groupInfo)
                Log.d("User_Info", "그룹 정보 저장 완료 : $groupInfo")
            }.onFailure {
                Log.e("User_Info", "그룹 정보 조회 실패: ${it.message}")
            }
            _isSaved.value = true
        }
    }

    // 소셜 로그인 함수
    fun loginWithSessionId(sessionId: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = authRepository.loginWithSession(sessionId)

            val data = result.getOrNull()
            val exception = result.exceptionOrNull()

            if (data != null) {
                // 토큰 저장
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                // userId 저장
                userInfoManager.saveUserId(data.userId)
                Log.d("User_Info", "userId 저장 완료 : ${data.userId}")
                // sessionId 삭제
                tokenManager.clearSessionId()
                Log.d("User_Info", "SessionId 삭제 완료")

                _loginState.value = LoginState.Success(data.flag)
            } else {
                val message = exception?.message ?: "알 수 없는 오류 발생"
                _loginState.value = LoginState.Error(message)
            }
        }
    }
}

// 로그인 상태 정의
sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val isNewUser: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}