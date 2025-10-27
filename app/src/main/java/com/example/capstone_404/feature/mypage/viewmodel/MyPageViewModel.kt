package com.example.capstone_404.feature.mypage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.AlarmRepository
import com.example.capstone_404.data.repository.UserRepository
import com.example.capstone_404.data.retrofit.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {

    // 사용자 닉네임 Flow
    val nicknameFlow: Flow<String?> = userInfoManager.nicknameFlow

    // 소셜 로그인 제공자 Flow
    val socialProviderFlow: Flow<String?> = userInfoManager.socialProviderFlow

    // 프로필 이미지 Flow
    val profileImageFlow: Flow<String?> = userInfoManager.profileImageFlow

    // 알림 설정 상태 Flow
    val alarmEnabledFlow: Flow<Boolean> = userInfoManager.alarmEnabledFlow

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 로그아웃 상태
    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    // 회원 탈퇴 상태
    private val _withdrawState = MutableStateFlow<WithdrawState>(WithdrawState.Idle)
    val withdrawState: StateFlow<WithdrawState> = _withdrawState

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }

    // 알림 설정 변경
    fun setAlarmNotification(enabled: Boolean) {
        viewModelScope.launch {
            val previousValue = userInfoManager.getAlarmEnabled()

            // UI 업데이트
            userInfoManager.saveAlarmEnabled(enabled)
            Log.d("MyPageViewModel", "알림 설정 UI 업데이트: $enabled")

            // api 연동
            alarmRepository.setAlarmSetting(enabled)
                .onSuccess {
                    Log.d("MyPageViewModel", "알림 설정 변경 성공: $enabled")
                }
                .onFailure { e ->
                    Log.e("MyPageViewModel", "알림 설정 변경 실패: ${e.message}")
                    // 실패 시 롤백
                    userInfoManager.saveAlarmEnabled(previousValue)
                    // 에러 Toast
                    _errorMessage.value = "알림 설정 변경 중 오류가 발생했습니다."
                }
        }
    }


    //로그아웃 처리
    fun logout() {
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            try {
                // 로그아웃 API 호출
                userRepository.logout()
                    .onSuccess {
                        Log.d("MyPageViewModel", "로그아웃 성공")
                        _logoutState.value = LogoutState.Success
                    }
                    .onFailure { exception ->
                        Log.e("MyPageViewModel", "로그아웃 실패: ${exception.message}")
                        _logoutState.value = LogoutState.Error(exception.message ?: "로그아웃 중 오류가 발생했습니다.")
                    }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "로그아웃 실패: ${e.message}")
                _logoutState.value = LogoutState.Error(e.message ?: "로그아웃 중 오류가 발생했습니다.")
            }
        }
    }


    //회원 탈퇴 처리
    fun withdraw() {
        viewModelScope.launch {
            _withdrawState.value = WithdrawState.Loading
            try {
                // 회원 탈퇴 API 호출
                userRepository.deleteUser()
                    .onSuccess {
                        Log.d("MyPageViewModel", "회원 탈퇴 성공")
                        _withdrawState.value = WithdrawState.Success
                    }
                    .onFailure { exception ->
                        Log.e("MyPageViewModel", "회원탈퇴 실패: ${exception.message}")
                        _withdrawState.value = WithdrawState.Error(exception.message ?: "회원탈퇴 중 오류가 발생했습니다.")
                    }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "회원탈퇴 실패: ${e.message}")
                _withdrawState.value = WithdrawState.Error(e.message ?: "회원탈퇴 중 오류가 발생했습니다.")
            }
        }
    }


    //상태 초기화
    fun resetLogoutState() {
        _logoutState.value = LogoutState.Idle
    }

    fun resetWithdrawState() {
        _withdrawState.value = WithdrawState.Idle
    }

    // 사용자 정보 로컬 저장
    fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserInfo()
                result.onSuccess {
                    Log.d("MyPageViewModel", "사용자 정보 로드 성공")
                }.onFailure { exception ->
                    Log.e("MyPageViewModel", "사용자 정보 로드 실패: ${exception.message}")
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "사용자 정보 로드 중 오류: ${e.message}")
            }
        }
    }
}

// 로그아웃 상태
sealed class LogoutState {
    data object Idle : LogoutState()
    data object Loading : LogoutState()
    data object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}

// 회원 탈퇴 상태
sealed class WithdrawState {
    data object Idle : WithdrawState()
    data object Loading : WithdrawState()
    data object Success : WithdrawState()
    data class Error(val message: String) : WithdrawState()
}