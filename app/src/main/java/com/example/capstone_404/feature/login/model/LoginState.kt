package com.example.capstone_404.feature.login.model

// 로그인 상태 정의
sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val isNewUser: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
}