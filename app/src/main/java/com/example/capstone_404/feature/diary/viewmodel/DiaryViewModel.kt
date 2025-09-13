package com.example.capstone_404.feature.diary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.DiaryRepository
import com.example.capstone_404.data.retrofit.model.response.DiaryData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.data.retrofit.model.response.GroupQuestionData
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.DiaryEntry
import com.example.capstone_404.feature.diary.model.DiaryState
import com.example.capstone_404.feature.diary.model.DiaryTab
import com.example.capstone_404.feature.diary.model.EmotionType
import com.example.capstone_404.feature.diary.model.roleLabelsToResponders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val groupInfoManager: GroupInfoManager,
    private val userInfoManager: UserInfoManager,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    // 그룹 정보 Flow
    val groupInfoFlow: Flow<GroupInfoData?> = groupInfoManager.groupInfoFlow

    // 화면 상태 관리
    private val _state = MutableStateFlow<DiaryState>(DiaryState.Success(emptyList(), emptyList()))
    val state: StateFlow<DiaryState> = _state

    // 선택된 탭 상태 관리
    private val _selectedTab = MutableStateFlow(DiaryTab.DIARY)
    val selectedTab: StateFlow<DiaryTab> = _selectedTab

    // 에러 메시지 상태 관리
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 콜백 함수
    fun switchTab(tab: DiaryTab) {
        _selectedTab.value = tab
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }

    fun refresh() {
        loadUserData()
    }

    private fun setLoadingState() {
        _state.value = DiaryState.Loading
    }

    // 빈 상태 (데이터 없음)
    private fun setEmptySuccessState() {
        _state.value = DiaryState.Success(
            diaryEntries = emptyList(),
            questions = emptyList()
        )
    }

    fun loadUserData() {
        viewModelScope.launch {
            setLoadingState()
            _errorMessage.value = null

            val currentUserId = userInfoManager.getUserId()
            if (currentUserId != null) {
                fetchUserData(currentUserId)
            } else {
                Log.e("DiaryViewModel", "사용자 ID를 찾을 수 없음")
                _errorMessage.value = "로그인 정보를 확인할 수 없습니다.\n다시 로그인해주세요."
                setEmptySuccessState()
            }
        }
    }

    // 다이어리 전체 조회
    private suspend fun fetchUserData(userId: Int) {
        val result = diaryRepository.getDiaryAndQuestions(userId)
        result.onSuccess { response ->
            Log.d("DiaryViewModel", "다이어리 조회 성공 : $response")

            val diaryEntries = response.diary.map { diary ->
                convertToDiaryEntry(diary)
            }
            val questions = response.questions.map { question ->
                convertToQuestion(question)
            }

            _state.value = DiaryState.Success(diaryEntries, questions)
        }.onFailure { error ->
            Log.e("DiaryViewModel", "다이어리 조회 실패 : ${error.message}")
            _errorMessage.value = "다이어리 정보 불러오기를 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요."
            setEmptySuccessState()
        }
    }

    private fun convertToDiaryEntry(diary: DiaryData): DiaryEntry {
        return DiaryEntry(
            id = diary.diId.toString(),
            date = formatDate(diary.date),
            emotion = mapEmotionStringToType(diary.emotion)
        )
    }

    private fun convertToQuestion(question: GroupQuestionData): Question {
        return Question(
            id = question.gqId.toString(),
            date = formatDate(question.date),
            question = "", // 메인화면에서는 질문 내용 불필요
            responders = roleLabelsToResponders(question.responders)
        )
    }

    // 날짜 포맷팅 (yyyy.MM.dd(E) 형식으로 변환)
    private fun formatDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString)
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
            formatter.format(date)
        } catch (e: Exception) {
            Log.e("DiaryViewModel", "날짜 파싱 실패: '$dateString'")
            dateString.replace("-", ".")
        }
    }

    // EmotionType 매핑
    private fun mapEmotionStringToType(emotion: String): EmotionType {
        return when (emotion) {
            "행복" -> EmotionType.HAPPINESS
            "혐오" -> EmotionType.DISGUST
            "놀람" -> EmotionType.SURPRISE
            "슬픔" -> EmotionType.SADNESS
            "분노" -> EmotionType.ANGER
            "불안" -> EmotionType.ANXIETY
            else -> EmotionType.HAPPINESS
        }
    }

    fun checkCanWriteDiary(
        onCanWrite: () -> Unit,
        onAlreadyWritten: () -> Unit
    ) {
        viewModelScope.launch {
            val groupId = userInfoManager.getGroupId()
            if (groupId == null) {
                Log.e("DiaryViewModel", "그룹 미가입 상태에서 다이어리 작성 시도")
                return@launch
            }

            val result = diaryRepository.getTodayQuestions(groupId)
            result.onSuccess {
                onCanWrite()
            }.onFailure { error ->
                val errorMessage = error.message ?: ""
                if (errorMessage.contains("오늘 해당 응답을 하셨습니다") || errorMessage.contains("500")) {
                    onAlreadyWritten()
                } else {
                    // 기타 네트워크 에러
                    onCanWrite()
                }
            }
        }
    }
}