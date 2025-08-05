package com.example.capstone_404.feature.login.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.UserRepository
import com.example.capstone_404.data.repository.GroupRepository
import com.example.capstone_404.data.retrofit.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.capstone_404.data.info.GroupInfo
import com.example.capstone_404.data.info.GroupUserInfo
import com.example.capstone_404.data.info.SurveyResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
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

    // 소셜 로그인 함수
    fun loginWithSessionId(sessionId: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = userRepository.loginWithSession(sessionId)

            val data = result.getOrNull()
            val exception = result.exceptionOrNull()

            if (data != null) {
                // 토큰 저장
                tokenManager.saveTokens(data.accessToken, data.refreshToken)
                // userId 저장
                userInfoManager.saveUserId(data.userId)
                Log.d("User_Info", "(L)userId 저장 완료 : ${data.userId}")
                // sessionId 삭제
                tokenManager.clearSessionId()
                Log.d("User_Info", "(L)SessionId 삭제 완료")

                _loginState.value = LoginState.Success(data.flag)
            } else {
                val message = exception?.message ?: "알 수 없는 오류 발생"
                _loginState.value = LoginState.Error(message)
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

    // DataStore에 [groupID] 저장
    fun saveGroupId() {
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
                    _isSaved.value = true
                }.onFailure { e ->
                    userInfoManager.deleteGroupId()
                    groupInfoManager.clearAll()
                    Log.d("User_Info", "(L)그룹 ID 조회 실패 : ${e.message}")
                    _isSaved.value = true
                }
            } catch (e: Exception) {
                userInfoManager.deleteGroupId()
                groupInfoManager.clearAll()
                Log.e("User_Info", "(L)그룹 ID 조회 실패 : ${e.message}")
                _isSaved.value = true
            }
        }
    }

    // DataStore에 [GroupInfo] 저장
    private suspend fun saveGroupInfo(groupId: Int) {
        val result = groupRepository.getGroupInfo(groupId)
        result.onSuccess { data ->
            val groupInfo = GroupInfo(
                groupName = data.group_name,
                groupImage = data.group_image,
                userinfo = data.userinfo.map {
                    GroupUserInfo(
                        userId = it.userId,
                        username = it.username,
                        role = it.role,
                        age = it.age,
                        image = it.image,
                        leader = it.leader
                    )
                }
            )
            groupInfoManager.saveGroupInfo(groupInfo)
            Log.d("User_Info", "(L)그룹 정보 저장 완료 : $groupInfo")
        }.onFailure {
            Log.e("User_Info", "(L)그룹 정보 조회 실패: ${it.message}")
        }
    }

    // DataStore에 [SurveyResult] 저장
    private suspend fun saveSurveyResult(groupId: Int) {
        val result = groupRepository.getSurveyResult(groupId)
        result.onSuccess { data ->
            val surveyResult = SurveyResult(
                level = data.level,
                score = data.score,
                percent = data.percent
            )
            groupInfoManager.saveSurveyResult(surveyResult)
            Log.d("User_Info", "(L)설문 결과 저장 완료 : $surveyResult")
        }.onFailure {
            Log.e("User_Info", "(L)설문 결과 저장 실패 : ${it.message}")
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