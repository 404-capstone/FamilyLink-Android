package com.example.capstone_404.feature.mypage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.UserInfoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userInfoManager: UserInfoManager
) : ViewModel() {

    private val _inputNickname = MutableStateFlow("")
    val inputNickname: StateFlow<String> = _inputNickname

    private val _selectedGender = MutableStateFlow("성별")
    val selectedGender: StateFlow<String> = _selectedGender

    private val _selectedAge = MutableStateFlow("연령대")
    val selectedAge: StateFlow<String> = _selectedAge

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    init {
        // 로컬 저장된 이름,성별, 연령대 불러오기
        viewModelScope.launch {
            userInfoManager.nicknameFlow.collect { nickname ->
                if (nickname != null && _inputNickname.value.isEmpty()) {
                    _inputNickname.value = nickname
                }
            }
        }

        viewModelScope.launch {
            userInfoManager.genderFlow.collect { gender ->
                if (gender != null) {
                    _selectedGender.value = gender
                }
            }
        }

        viewModelScope.launch {
            userInfoManager.ageFlow.collect { age ->
                if (age != null) {
                    _selectedAge.value = age
                }
            }
        }
    }

    //닉네임 입력 업데이트
    fun updateNickname(nickname: String) {
        _inputNickname.value = nickname
    }

    // 성별 선택
    fun selectGender(gender: String) {
        _selectedGender.value = gender
    }

    //연령대 선택
    fun selectAge(age: String) {
        _selectedAge.value = age
    }


    //프로필 저장 - api 추가 전 임시 구현
    fun saveProfile() {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                // TODO: API 호출로 프로필 정보 업데이트
                //로컬 저장 처리
                userInfoManager.saveNickname(_inputNickname.value)

                if (_selectedGender.value != "성별") {
                    userInfoManager.saveGender(_selectedGender.value)
                }
                if (_selectedAge.value != "연령대") {
                    userInfoManager.saveAge(_selectedAge.value)
                }

                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "저장 중 오류가 발생했습니다.")
            }
        }
    }

    //저장 상태 초기화
    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}

sealed class SaveState {
    data object Idle : SaveState()
    data object Loading : SaveState()
    data object Success : SaveState()
    data class Error(val message: String) : SaveState()
}