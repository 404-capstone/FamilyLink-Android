package com.example.capstone_404.feature.login.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.repository.AuthRepository
import com.example.capstone_404.data.retrofit.model.response.KakaoLoginData
import com.example.capstone_404.data.retrofit.token.TokenManager
import com.example.capstone_404.social.naver.NaverAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // 자동 로그인 용 상태 관리
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // 카카오 로그인 상태 관리
    private val _kakaoLoginResult = MutableStateFlow<Result<KakaoLoginData>?>(null)
    val kakaoLoginResult: StateFlow<Result<KakaoLoginData>?> = _kakaoLoginResult

    // 자동 로그인 체크 함수
    fun checkAutoLogin() {
        viewModelScope.launch {
            _isLoggedIn.value = tokenManager.hasValidToken()
        }
    }

    // 소셜 로그인 상태 관리
    var socialLoginState by mutableStateOf<Result<String>?>(null)
        private set

    // 카카오 로그인 함수
    fun loginWithKakao() {
        viewModelScope.launch {
            val result = authRepository.loginWithKakao()
            _kakaoLoginResult.value = result
        }
    }

    // 네이버 로그인 함수
    fun loginWithNaver(context: Context) {
        viewModelScope.launch {
            try {
                NaverAuthManager.login(context)
                Log.d("NaverLogin", "NaverLogin start")
            } catch (e: Exception) {
                socialLoginState = Result.failure(e)
                Log.e("NaverLogin", "NaverLogin failed: ${e.message}")
            }
        }
    }

    // 토큰 저장 함수
    fun saveTokens(accessToken: String, refreshToken: String) {
        viewModelScope.launch {
            tokenManager.saveTokens(accessToken, refreshToken)
        }
    }
}