package com.example.capstone_404.feature.mypage.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.R
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.UserRepository
import com.example.capstone_404.data.retrofit.model.request.UserInfoEditRequest
import com.example.capstone_404.utils.AgeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userInfoManager: UserInfoManager,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _inputNickname = MutableStateFlow("")
    val inputNickname: StateFlow<String> = _inputNickname

    private val _selectedGender = MutableStateFlow("성별")
    val selectedGender: StateFlow<String> = _selectedGender

    private val _selectedAge = MutableStateFlow("연령대")
    val selectedAge: StateFlow<String> = _selectedAge

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState

    // 프로필 이미지 상태
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    // 프로필 이미지 Flow
    val profileImageFlow: Flow<String?> = userInfoManager.profileImageFlow

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

    // 이미지 선택
    fun selectImage(imageUri: Uri?) {
        _selectedImageUri.value = imageUri
    }

    // 기본 이미지 사용
    fun useDefaultImage() {
        val defaultImageUri = "android.resource://${context.packageName}/${R.drawable.default_profile}".toUri()
        _selectedImageUri.value = defaultImageUri
    }


    //프로필 저장
    fun saveProfile() {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                // 연령대를 숫자로 변환
                val ageNumber = if (_selectedAge.value != "연령대") {
                    AgeConverter.convertRangeToAge(_selectedAge.value)?: 0
                } else {
                    0 // 기본값
                }

                // 이미지 URI를 문자열로 변환
                val imageString = _selectedImageUri.value?.toString()

                // API 요청 객체 생성
                val request = UserInfoEditRequest(
                    username = _inputNickname.value.trim(),
                    age = ageNumber,
                    gender = if (_selectedGender.value != "성별") _selectedGender.value else "",
                    image = imageString
                )

                // API 호출
                userRepository.editUserInfo(request)
                    .onSuccess { response ->
                        Log.d("ProfileEditViewModel", "프로필 수정 성공: ${response}")

                        // 로컬 저장 처리
                        userInfoManager.saveNickname(_inputNickname.value)

                        if (_selectedGender.value != "성별") {
                            userInfoManager.saveGender(_selectedGender.value)
                        }
                        if (_selectedAge.value != "연령대") {
                            userInfoManager.saveAge(_selectedAge.value)
                        }

                        // 프로필 이미지 저장
                        imageString?.let {
                            userInfoManager.saveProfileImage(it)
                        }

                _saveState.value = SaveState.Success
                    }
                    .onFailure { exception ->
                        Log.e("ProfileEditViewModel", "프로필 수정 실패: ${exception.message}")
                        _saveState.value = SaveState.Error(exception.message ?: "프로필 수정 중 오류가 발생했습니다.")
                    }
            } catch (e: Exception) {
                Log.e("ProfileEditViewModel", "프로필 수정 실패: ${e.message}")
                _saveState.value = SaveState.Error(e.message ?: "프로필 수정 중 오류가 발생했습니다.")
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