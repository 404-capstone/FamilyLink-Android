package com.example.capstone_404.ui.login.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.social.kakao.KakaoAuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    // 카카오 로그인 상태 관리
    var socialLoginState by mutableStateOf<Result<String>?>(null)
        private set

    // 카카오 로그인 함수
    fun loginWithKakao(context: Context) {
        viewModelScope.launch {
            try {
                // 임시 진행
                val token = KakaoAuthManager.login(context)
                socialLoginState = Result.success(token)
                Log.d("Kakao_token",token)
            } catch (e: Exception) {
                socialLoginState = Result.failure(e)
            }
        }
    }
}