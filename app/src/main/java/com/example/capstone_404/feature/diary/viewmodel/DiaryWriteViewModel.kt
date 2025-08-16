package com.example.capstone_404.feature.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class DiaryWriteViewModel @Inject constructor() : ViewModel() {
    
    companion object {
        private const val MIN_DIARY_LENGTH = 50 // 최소 다이어리 길이(50자)
        private const val LOADING_DELAY = 1500L // 로딩 지연 시간
    }
    
    private val _uiState = MutableStateFlow(DiaryWriteState())
    val uiState: StateFlow<DiaryWriteState> = _uiState
    
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
}
