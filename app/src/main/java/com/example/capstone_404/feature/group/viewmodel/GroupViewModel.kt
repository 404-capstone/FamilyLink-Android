package com.example.capstone_404.feature.group.viewmodel

import android.content.Context
import android.content.Intent
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
import com.example.capstone_404.data.info.GroupInfo
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.GroupUserInfo
import com.example.capstone_404.data.info.SurveyResult
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.feature.group.model.InviteCodeStatus
import com.example.capstone_404.feature.group.model.calculateSurveyResult
import com.example.capstone_404.feature.group.model.inviteMessage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle,
    private val userInfoManager: UserInfoManager,
    private val groupInfoManager: GroupInfoManager
) : ViewModel() {
    // -------------------- 상태 변수 --------------------
    // 그룹 정보 Flow
    val groupInfoFlow: Flow<GroupInfo?> = groupInfoManager.groupInfoFlow
    // 유저 ID Flow
    val userIdFlow: Flow<Int?> = userInfoManager.userIdFlow
    // 설문 결과 Flow
    val surveyResultFlow: Flow<SurveyResult?> = groupInfoManager.surveyResultFlow

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

    // 초대 코드 상태 (로딩, 없음, 발급, 만료)
    private val _inviteCodeStatus = MutableStateFlow(InviteCodeStatus.LOADING)
    val inviteCodeStatus: StateFlow<InviteCodeStatus> = _inviteCodeStatus.asStateFlow()

    // 초대 코드 값
    private val _inviteCodeValue = MutableStateFlow<String?>(null)
    val inviteCodeValue: StateFlow<String?> = _inviteCodeValue.asStateFlow()

    // 코드 기반 조회 그룹 정보
    private val _groupInfoByCode = MutableStateFlow<GroupInfo?>(null)
    val groupInfoByCode: StateFlow<GroupInfo?> = _groupInfoByCode

    // 코드 기반 조회 결과
    private val _getError = MutableStateFlow(false)
    val getError: StateFlow<Boolean> = _getError


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
    // 초대 코드 기반 그룹 정보 조회
    fun getGroupByInviteCode(code: String) {
        viewModelScope.launch {
            isLoading = true
            _getError.value = false
            // 코드 기반 그룹 ID 조회
            val groupIdResult = groupRepository.getGroupIdByCode(code)
            groupIdResult.onSuccess { groupId ->
                Log.d("GroupViewModel", "코드 기반 그룹 ID 조회 완료 : $groupId")
                // 그룹 정보 조회
                val groupInfoResult = groupRepository.getGroupInfo(groupId)
                groupInfoResult.onSuccess { data ->
                    Log.d("GroupViewModel", "코드 기반 그룹 정보 조회 완료 : $data")
                    _groupInfoByCode.value = GroupInfo(
                        groupName = data.group_name,
                        groupImage = data.group_image ?: "",
                        userinfo = data.userinfo.map {
                            GroupUserInfo(
                                userId = it.userId,
                                username = it.username,
                                role = it.role,
                                age = it.age ?: "연령대 미지정",
                                image = it.image ?: "",
                                leader = it.leader
                            )
                        }
                    )
                }.onFailure {
                    Log.d("GroupViewModel", "코드 기반 그룹 정보 조회 실패 : ${it.message}")
                    _getError.value = true
                }
            }.onFailure {
                Log.d("GroupViewModel", "코드 기반 그룹 ID 조회 실패 : ${it.message}")
                _getError.value = true
            }
            isLoading = false
        }
    }
    // 에러 상태 초기화
    fun resetInviteError() {
        _getError.value = false
    }
    // 조회 정보 초기화
    fun resetGroupInfoByCode() {
        _groupInfoByCode.value = null
    }
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
                Log.d("GroupViewModel", "그룹 가입 요청 : code=$inviteCode, role=$roleLabel")

                val result = groupRepository.joinGroup(inviteCode, roleLabel)

                result.onSuccess { data ->
                    Log.d("GroupViewModel", "그룹 가입 완료 : $data")
                    groupEntrySuccess(data.groupId, result)
                }.onFailure {
                    Log.d("GroupViewModel", "그룹 가입 실패 : ${it.message}")
                }
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

                result.onSuccess { data ->
                    Log.d("GroupViewModel", "그룹 생성 완료 : $data")
                    groupEntrySuccess(data.groupId, result)
                }.onFailure {
                    Log.d("GroupViewModel", "그룹 생성 실패 : ${it.message}")
                }
            }
        }
    }
    // 그룹 생성|가입 성공 시 저장 함수
    private suspend fun groupEntrySuccess(groupId: Int, result: Result<GroupData>) {
        // groupId 저장
        userInfoManager.saveGroupId(groupId)
        Log.d("User_Info", "(G) 그룹 ID 저장 완료 : $groupId")
        // 그룹 정보 저장
        coroutineScope {
            val groupInfoDeferred = async { saveGroupInfo(groupId) }
            groupInfoDeferred.await()
        }
        _createResult.value = result
        Log.d("GroupViewModel", "그룹 생성|가입 결과 : ${_createResult.value}")
    }
    // DataStore에 [GroupInfo] 저장
    private suspend fun saveGroupInfo(groupId: Int) {
        val result = groupRepository.getGroupInfo(groupId)
        result.onSuccess { data ->
            val groupInfo = GroupInfo(
                groupName = data.group_name,
                groupImage = data.group_image ?: "",
                userinfo = data.userinfo.map {
                    GroupUserInfo(
                        userId = it.userId,
                        username = it.username,
                        role = it.role,
                        age = it.age ?: "연령대 미지정",
                        image = it.image ?: "",
                        leader = it.leader
                    )
                }
            )
            groupInfoManager.saveGroupInfo(groupInfo)
            Log.d("User_Info", "(G)그룹 정보 저장 완료 : $groupInfo")
        }.onFailure {
            Log.e("User_Info", "(G)그룹 정보 조회 실패: ${it.message}")
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
    // 설문 응답 제출 처리
    fun submitSurvey() {
        isLoading = true
        viewModelScope.launch {
            val totalScore = _surveyResponses.values.sum()
            val surveyResult = calculateSurveyResult(totalScore)
            val groupId = userInfoManager.getGroupId()
            Log.d("GroupViewModel","설문 결과 : $surveyResult")
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
                        groupInfoManager.saveSurveyResult(surveyResult)
                        Log.d("User_Info", "(G)설문 결과 저장 완료 : $surveyResult")
                        Log.d("GroupViewModel", "설문 결과 저장 완료 : $data")
                        _isSaved.value = true
                        isLoading = false
                    }.onFailure { e ->
                        Log.d("GroupViewModel", "설문 결과 저장 실패 : ${e.message}")
                        _isSaved.value = false
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.d("GroupViewModel", "설문 결과 저장 실패 : ${e.message}")
                _isSaved.value = false
                isLoading = false
            }
        }
    }

    // -------------------- 그룹원 초대 함수 --------------------
    // 초대 코드 조회
    suspend fun fetchInviteCode() {
        _inviteCodeStatus.value = InviteCodeStatus.LOADING
        val groupId = userInfoManager.getGroupId()
        viewModelScope.launch {
            val result = groupId?.let { groupRepository.getInviteCode(it) }

            result?.onSuccess { data ->
                Log.d("GroupViewModel", "초대 코드 조회 완료 : $data")
                _inviteCodeValue.value = data
                _inviteCodeStatus.value = InviteCodeStatus.GENERATED
            }?.onFailure { error ->
                val errorMessage = error.message.orEmpty()
                Log.d("GroupViewModel", "초대 코드 조회 실패 : $errorMessage")
                when {
                    errorMessage.contains("초대코드가 만료되었습니다") -> {
                        Log.d("GroupViewModel", "초대 코드 조회 실패 처리 : 만료")
                        _inviteCodeValue.value = null
                        _inviteCodeStatus.value = InviteCodeStatus.EXPIRED
                    }
                    else -> {
                        Log.d("GroupViewModel", "초대 코드 조회 실패 처리 : 없음")
                        _inviteCodeValue.value = null
                        _inviteCodeStatus.value = InviteCodeStatus.NOT_GENERATED
                    }
                }
            }
        }
    }
    // 초대 코드 생성
    fun createInviteCode() {
        _inviteCodeStatus.value = InviteCodeStatus.LOADING
        viewModelScope.launch {
            val groupId = userInfoManager.getGroupId()
            val result = groupId?.let { groupRepository.createInviteCode(it) }

            result?.onSuccess { code ->
                Log.d("GroupViewModel", "초대 코드 생성 완료 : $code")
                _inviteCodeValue.value = code
                _inviteCodeStatus.value = InviteCodeStatus.GENERATED
            }?.onFailure { e ->
                _inviteCodeValue.value = null
                _inviteCodeStatus.value = InviteCodeStatus.NOT_GENERATED
                Log.e("GroupViewModel", "초대 코드 생성 실패: ${e.message}")
            }
        }
    }
    // 초대 코드 공유(텍스트 임시)
    fun shareInviteCode(context: Context) {
        val message = inviteMessage(
            code = _inviteCodeValue.value.toString(),
            inviteUrl = "",
            downloadUrl = ""
        )

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "초대 코드 공유")
        context.startActivity(shareIntent)
    }
}