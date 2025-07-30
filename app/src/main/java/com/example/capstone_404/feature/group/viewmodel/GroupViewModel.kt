package com.example.capstone_404.feature.group.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.repository.GroupRepository
import com.example.capstone_404.data.retrofit.model.response.GroupData
import com.example.capstone_404.feature.group.model.OrderType
import com.example.capstone_404.feature.group.model.RoleType
import com.example.capstone_404.feature.group.model.SelectedRoleState
import com.example.capstone_404.feature.group.model.toKorean
import com.example.capstone_404.utils.prepareImagePart
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.feature.group.model.calculateSurveyResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    private val userInfoManager: UserInfoManager
) : ViewModel() {
    // -------------------- 상태 변수 --------------------
    // 그룹 가입 여부
    var isJoined by mutableStateOf(false)
        private set

    // 역할 선택 상태
    var selectedRoleState by mutableStateOf(SelectedRoleState())
        private set

    // 로딩 출력 여부
    var isLoading by mutableStateOf(false)
        private set

    // 그룹 생성 결과
    private val _createResult = MutableStateFlow<Result<GroupData>?>(null)
    val createResult: StateFlow<Result<GroupData>?> = _createResult

    // 사용자 설문 응답 저장
    private val _surveyResponses = mutableStateMapOf<Int, Int>()
    val surveyResponses: Map<Int, Int> get() = _surveyResponses

    // 서버에 설문 결과 저장의 결과
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved


    // -------------------- 생성|가입에 필요한 전달 데이터 --------------------
    private val groupName = savedStateHandle["groupName"] ?: ""
    private val imageUri = savedStateHandle.get<String>("imageUri")?.takeIf { it.isNotBlank() }?.toUri()
    private val inviteCode = savedStateHandle["inviteCode"] ?: ""


    // -------------------- 역할 선택 함수 --------------------
    // 역할 선택 상태 변경
    fun updateSelectedRole(role: RoleType?) {
        selectedRoleState = selectedRoleState.copy(role = role)
        if (role != RoleType.SON && role != RoleType.DAUGHTER) {
            selectedRoleState = selectedRoleState.copy(order = null)
        }
    }
    // 아들&딸 Order 변경
    fun updateSelectedOrder(order: OrderType?) {
        selectedRoleState = selectedRoleState.copy(order = order)
    }


    // -------------------- 가입|생성 함수 --------------------
    // 가입 or 생성에 따른 제출 처리
    fun submitGroupEntry() {
        val role = selectedRoleState.role ?: return
        val roleLabel = if (role == RoleType.SON || role == RoleType.DAUGHTER) {
            val order = selectedRoleState.order ?: return
            "${order.label} ${role.toKorean()}"
        } else {
            role.toKorean()
        }
        viewModelScope.launch {
            if (inviteCode.isNotBlank()) {
                // Todo : 그룹 가입 API 호출 예정
                Log.d("GroupViewModel", "그룹 가입 요청: code=$inviteCode, role=$roleLabel")
//                _createResult.value = groupRepository.JoinGroup(inviteCode, roleLabel)
                Log.d("GroupViewModel", "가입 결과: $_createResult")
            } else {
                // 이미지 없는 경우 empty value 전달
                val imagePart = imageUri?.let { prepareImagePart(appContext, it) }
                    ?: MultipartBody.Part.createFormData(
                        name = "image",
                        filename = "",
                        body = "".toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    )
                Log.d("GroupViewModel", "그룹 생성 요청: groupName : $groupName, role : $roleLabel, image : $imagePart")

                val result = groupRepository.createGroup(groupName, roleLabel, imagePart)

                val data = result.getOrNull()
                val exception = result.exceptionOrNull()
                if (data != null) {
                    // groupId 저장
                    userInfoManager.saveGroupId(data.groupId)
                    _createResult.value = result
                } else {
                    Log.d("GroupViewModel", "생성 실패 : $exception")
                }
                Log.d("GroupViewModel", "생성 결과: $_createResult")
            }
        }
    }


    // -------------------- 설문지 함수 --------------------
    // 응답 선택
    fun onAnswerSelected(questionId: Int, selectedOption: Int) {
        _surveyResponses[questionId] = selectedOption
    }
    // 버튼 활성화(모든 설문에 대한 응답 선택 시)
    fun isSurveySubmitEnabled(totalQuestions: Int): Boolean {
        return _surveyResponses.size == totalQuestions
    }
    // 제출 처리
    fun submitSurvey() {
        isLoading = true
        viewModelScope.launch {
            val totalScore = _surveyResponses.values.sum()
            val surveyResult = calculateSurveyResult(totalScore)
            val groupId = userInfoManager.getGroupId()
            Log.d("GroupScreen", "Level: ${surveyResult.level}\n Score: ${surveyResult.score}\n Percent: ${surveyResult.percent}%")
            try {
                val result = groupId?.let {
                    groupRepository.saveSurveyResult(
                        groupId = it,
                        level = surveyResult.level,
                        score = surveyResult.score,
                        percent = surveyResult.percent
                    )
                }
                if (result != null) {
                    result.onSuccess { data ->
                        Log.d("GroupScreen", "설문 결과 저장 완료 : $data")
                        _isSaved.value = true
                        isLoading = false
                    }.onFailure { e ->
                        Log.d("GroupScreen", "설문 결과 저장 실패 : ${e.message}")
                        _isSaved.value = false
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.d("GroupScreen", "설문 결과 저장 실패 : ${e.message}")
                _isSaved.value = false
                isLoading = false
            }
        }
    }
}