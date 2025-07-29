package com.example.capstone_404.feature.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.AuthRepository
import com.example.capstone_404.data.retrofit.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager
) : ViewModel() {

    // 소셜 로그인 상태 관리
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // 자동 로그인 용 상태 관리
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

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
                Log.d("User_Info", "${data.userId} 저장 완료")

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