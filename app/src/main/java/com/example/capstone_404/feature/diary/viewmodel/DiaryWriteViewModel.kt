package com.example.capstone_404.feature.diary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.DiaryRepository
import com.example.capstone_404.data.retrofit.model.response.GroupQuestionItem
import com.example.capstone_404.feature.diary.model.DiaryWriteState
import com.example.capstone_404.feature.diary.model.WriteStep
import com.example.capstone_404.feature.diary.model.FeedbackResult
import com.example.capstone_404.feature.diary.model.EmotionResult
import com.example.capstone_404.feature.diary.model.EmotionColorConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userInfoManager: UserInfoManager
) : ViewModel() {
    
    companion object {
        private const val MIN_DIARY_LENGTH = 50 // 최소 다이어리 길이(50자)
        private const val LOADING_DELAY = 1500L // 로딩 지연 시간
    }
    
    private val _uiState = MutableStateFlow(DiaryWriteState())
    val uiState: StateFlow<DiaryWriteState> = _uiState

    // 질문지 조회 데이터
    private val _todayQuestions = MutableStateFlow<List<GroupQuestionItem>>(emptyList())

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 콜백 함수
    fun onDiaryChange(text: String) {
        handleDiaryTextChange(text)
    }
    
    fun onNextFromDiary() {
        handleNextFromDiary()
    }
    
    fun onAnswerChange(index: Int, text: String) {
        handleAnswerChange(index, text)
    }
    
    fun onPrevToDiary() {
        handlePrevToDiary()
    }
    
    fun onSubmit() {
        handleSubmit()
    }
    
    private fun handleDiaryTextChange(text: String) {
        _uiState.update { currentState ->
            currentState.copy(
                diaryText = text,
                isDiaryEmpty = text.isBlank(),
                isDiaryTooShort = text.isNotBlank() && text.length < MIN_DIARY_LENGTH
            )
        }
    }

    private fun handleNextFromDiary() {
        val currentState = _uiState.value
        if (!currentState.isDiaryEmpty && !currentState.isDiaryTooShort) {
            if (_todayQuestions.value.isEmpty()) {
                loadTodayQuestions(
                    onError = { errorMsg ->
                        _errorMessage.value = errorMsg
                    }
                )
            }
            _uiState.update { it.copy(step = WriteStep.QUESTION) }
        }
    }
    
    private fun handleAnswerChange(index: Int, text: String) {
        _uiState.update { state ->
            val updated = state.answerTexts.toMutableList()
            if (index in updated.indices) updated[index] = text
            state.copy(answerTexts = updated)
        }
    }
    
    private fun handlePrevToDiary() {
        _uiState.update { it.copy(step = WriteStep.DIARY) }
    }
    
    private fun handleSubmit() {
        _uiState.update { it.copy(step = WriteStep.LOADING) }
        
        viewModelScope.launch {
            delay(LOADING_DELAY)
            
            // 피드백 결과 테스트 데이터
            val testResult = testFeedbackResult(_uiState.value.diaryText)
            
            _uiState.update { 
                it.copy(
                    step = WriteStep.RESULT,
                    result = testResult
                )
            }
        }
    }
    
    // 테스트용 피드백 결과 생성
    private fun testFeedbackResult(diaryText: String): FeedbackResult {
        val emotions = listOf(
            EmotionResult("기쁨", 0.452f, EmotionColorConstants.JOY),
            EmotionResult("혐오", 0.210f, EmotionColorConstants.DISGUST),
            EmotionResult("놀람", 0.180f, EmotionColorConstants.SURPRISE),
            EmotionResult("슬픔", 0.098f, EmotionColorConstants.SADNESS),
            EmotionResult("분노", 0.035f, EmotionColorConstants.ANGER),
            EmotionResult("상처", 0.025f, EmotionColorConstants.HURT)
        )
        
        val aiFeedback = """
            AI가 작성한 피드백입니다. 반갑습니다.
            안녕하세요. 어려웠을 하루를
            잘 보내셨네요.
        """.trimIndent()
        
        return FeedbackResult(
            diaryText = diaryText,
            emotionResults = emotions,
            aiFeedback = aiFeedback
        )
    }
    
    // 다이어리 작성 가능 여부 확인
    fun canProceedFromDiary(): Boolean {
        val state = _uiState.value
        return !state.isDiaryEmpty && !state.isDiaryTooShort
    }
    
    // 도움말 메시지 생성
    fun getHelperMessage(): String {
        val state = _uiState.value
        return when {
            state.isDiaryEmpty -> "작성한 일기는 본인만 열람이 가능하며, 다른 사용자에게 공유되지 않습니다."
            state.isDiaryTooShort -> "감정 분석을 위해 최소 50자 이상 작성해주세요. (현재 ${state.diaryText.length}자)"
            else -> "작성한 일기는 본인만 열람이 가능하며, 다른 사용자에게 공유되지 않습니다."
        }
    }

    // 금일 질문지 로드
    fun loadTodayQuestions(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            val groupId = userInfoManager.getGroupId()
            if (groupId == null) {
                val errorMsg = "그룹 정보를 찾을 수 없습니다.\n다시 로그인해주세요."
                Log.e("DiaryWriteViewModel", errorMsg)
                onError(errorMsg)
                return@launch
            }

            val result = diaryRepository.getTodayQuestions(groupId)
            result.onSuccess { response ->
                Log.d("DiaryWriteViewModel", "오늘의 질문 로드 성공: $response")
                _todayQuestions.value = response.groupQuestion

                // 질문 텍스트와 답변 리스트 초기화
                _uiState.update { state ->
                    state.copy(
                        questionTexts = response.groupQuestion.map { it.content },
                        answerTexts = List(response.groupQuestion.size) { "" }
                    )
                }
                // 에러 메시지 초기화
                _errorMessage.value = null
                onSuccess()
            }.onFailure { error ->
                Log.e("DiaryWriteViewModel", "오늘의 질문 로드 실패: ${error.message}")
                // 실패 시 빈 질문으로 초기화
                _todayQuestions.value = emptyList()
                _uiState.update { state ->
                    state.copy(
                        questionTexts = emptyList(),
                        answerTexts = emptyList()
                    )
                }
                onError(error.message ?: "질문을 불러오는데 실패했습니다.")
            }
        }
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}
