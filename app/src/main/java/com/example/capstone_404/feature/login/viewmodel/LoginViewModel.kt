package com.example.capstone_404.feature.login.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.UserRepository
import com.example.capstone_404.data.repository.GroupRepository
import com.example.capstone_404.data.retrofit.token.TokenManager
import com.example.capstone_404.feature.login.model.LoginState
import com.example.capstone_404.utils.AgeConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    val tokenManager: TokenManager,
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager
) : ViewModel() {
    // 사용자 정보 Flow
    val nicknameFlow: Flow<String?> = userInfoManager.nicknameFlow
    val userGenderFlow: Flow<String?> = userInfoManager.genderFlow
    val userAgeFlow: Flow<String?> = userInfoManager.ageFlow

    // 소셜 로그인 상태 관리
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // 자동 로그인용 상태 관리
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    // 그룹 ID 저장 상태 관리
    private val _isSaved = MutableStateFlow<Boolean?>(null)
    val isSaved: StateFlow<Boolean?> = _isSaved

    // FCM 토큰 상태 관리
    private val _fcmToken = MutableStateFlow<String?>(null)
    val fcmToken: StateFlow<String?> = _fcmToken

    // 선택된 추가 정보 변수
    var selectedGender by mutableStateOf("")
        private set
    var selectedAge by mutableStateOf("")
        private set

    // 자동 로그인 체크 함수
    fun checkAutoLogin() {
        viewModelScope.launch {
            _isLoggedIn.value = tokenManager.hasValidToken()
        }
    }

    // FCM 토큰 설정
    fun setFcmToken(fcmToken: String?) {
        _fcmToken.value = fcmToken
        Log.d("LoginViewModel", "FCM 토큰 설정: ${fcmToken?.take(10)}...")
    }

    // 소셜 로그인 함수
    fun loginWithSessionId(sessionId: String, fcmToken: String? = null) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val result = userRepository.loginWithSession(sessionId, fcmToken)

                result.onSuccess { data ->
                    Log.d("LoginViewModel", "소셜 로그인 성공 : $data")
                    _loginState.value = LoginState.Success(data.flag)
                }.onFailure { e ->
                    Log.d("LoginViewModel", "소셜 로그인 실패 : ${e.message}")
                    _loginState.value = LoginState.Error(e.message.toString())
                }
            } catch (e: Exception) {
                Log.d("LoginViewModel", "소셜 로그인 실패 : ${e.message}")
                _loginState.value = LoginState.Error(e.message.toString())
            }
        }
    }

    // -------------------- 앱 시작 시 사용자 정보 저장 함수 --------------------
    // Todo : 하단에 조회 API 연결한 저장 함수 추가
    // DataStore에 [SessionId] 저장
    fun saveSessionId(sessionId: String) {
        viewModelScope.launch {
            tokenManager.saveSessionId(sessionId)
        }
    }

    // DataStore에 [UserInfo] 저장
    fun saveUserInfo() {
        viewModelScope.launch {
            try {
                val result = userRepository.getUserInfo()

                result.onSuccess { data ->
                    Log.d("User_Info", "(L)사용자 Info 조회 성공 : $data")
                    coroutineScope {
                        val groupIdDeferred = async { saveGroupId() }
                        groupIdDeferred.await()
                    }
                    _isSaved.value = true
                }.onFailure { e ->
                    Log.d("User_Info", "(L)사용자 Info 조회 실패 : ${e.message}")
                    _isSaved.value = true
                }
            } catch (e: Exception) {
                Log.e("User_Info", "(L)사용자 Info 조회 실패 : ${e.message}")
                _isSaved.value = true
            }
        }
    }

    // DataStore에 [groupID] 저장
    private fun saveGroupId() {
        viewModelScope.launch {
            try {
                val result = groupRepository.getGroupIdFromServer()

                result.onSuccess { groupId ->
                    userInfoManager.saveGroupId(groupId)
                    Log.d("User_Info", "(L)그룹 ID 저장 완료 : $groupId")
                    // 사용자 정보 저장 병렬 처리
                    // Todo : 조회 API 다 여기에 연결
                    coroutineScope {
                        val groupInfoDeferred = async { saveGroupInfo(groupId) }
                        val surveyResultDeferred = async { saveSurveyResult(groupId) }
                        // 처리가 끝날 때까지 대기
                        groupInfoDeferred.await()
                        surveyResultDeferred.await()
                    }
                }.onFailure { e ->
                    userInfoManager.deleteGroupId()
                    groupInfoManager.clearAll()
                    Log.d("User_Info", "(L)그룹 ID 조회 실패 : ${e.message}")
                }
            } catch (e: Exception) {
                userInfoManager.deleteGroupId()
                groupInfoManager.clearAll()
                Log.e("User_Info", "(L)그룹 ID 조회 실패 : ${e.message}")
            }
        }
    }

    // DataStore에 [GroupInfo] 저장
    private suspend fun saveGroupInfo(groupId: Int) {
        val result = groupRepository.getGroupInfo(groupId)
        result.onSuccess { data ->
            groupInfoManager.saveGroupInfo(data)
            Log.d("User_Info", "(L)그룹 정보 저장 완료 : $data")
        }.onFailure {
            groupInfoManager.clearGroupInfo()
            Log.e("User_Info", "(L)그룹 정보 조회 실패: ${it.message}")
        }
    }

    // DataStore에 [SurveyResult] 저장
    private suspend fun saveSurveyResult(groupId: Int) {
        val result = groupRepository.getSurveyResult(groupId)
        result.onSuccess { data ->
            groupInfoManager.saveSurveyResult(data)
            Log.d("User_Info", "(L)설문 결과 저장 완료 : $data")
        }.onFailure {
            groupInfoManager.clearSurveyResult()
            Log.e("User_Info", "(L)설문 결과 저장 실패 : ${it.message}")
        }
    }

    // saved 초기화
    fun resetSaved() {
        _isSaved.value = null
    }

    // 추가 정보 변수값 변경 함수
    fun selectGender(gender: String) {
        selectedGender = gender
    }
    fun selectAge(age: String) {
        selectedAge = age
    }
    // 버튼 활성화 함수
    fun isProfileComplete(): Boolean {
        return selectedGender.isNotBlank() && selectedAge.isNotBlank()
    }
    // 정보 제출 함수
    fun submitInfo() {
        viewModelScope.launch {
            try {
                val userName = userInfoManager.getNickname()
                val ageNumber = AgeConverter.convertRangeToAge(selectedAge)
                val imagePart = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = "",
                    body = "".toRequestBody("application/octet-stream".toMediaTypeOrNull())
                )

                val result = userRepository.editUserInfo(userName!!, ageNumber!!, selectedGender, imagePart)

                result.onSuccess { data ->
                    Log.d("User_Info", "(L)사용자 정보 저장 완료 : $data")
                    _isSaved.value = true
                }.onFailure { e ->
                    Log.d("User_Info", "(L)사용자 정보 저장 실패 : ${e.message}")
                    _isSaved.value = false
                }
            } catch (e: Exception) {
                Log.d("User_Info", "(L)사용자 정보 저장 실패 : ${e.message}")
                _isSaved.value = false
            }
        }
    }
}