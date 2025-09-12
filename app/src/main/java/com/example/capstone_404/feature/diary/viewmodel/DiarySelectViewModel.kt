package com.example.capstone_404.feature.diary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.repository.DiaryRepository
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.feature.diary.model.DiaryDetail
import com.example.capstone_404.feature.diary.model.QuestionDetail
import com.example.capstone_404.feature.diary.model.DiarySelectUiState
import com.example.capstone_404.feature.diary.model.EmotionResult
import com.example.capstone_404.feature.diary.model.EmotionColorConstants
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.QuestionWithAnswers
import com.example.capstone_404.feature.diary.model.roleLabelsToResponders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiarySelectViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    // 상태 관리
    private val _diarySelectState = MutableStateFlow<DiarySelectUiState>(DiarySelectUiState.Loading)
    val diarySelectState: StateFlow<DiarySelectUiState> = _diarySelectState.asStateFlow()

    // 비즈니스 로직
    fun selectDiary(id: String) {
        viewModelScope.launch {
            _diarySelectState.value = DiarySelectUiState.Loading

            val diaryId = id.toLongOrNull()
            if (diaryId == null) {
                Log.e("DiarySelectViewModel", "잘못된 다이어리 ID: $id")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorDiaryDetail())
                return@launch
            }

            val result = diaryRepository.getDiaryDetail(diaryId)
            result.onSuccess { response ->
                Log.d("DiarySelectViewModel", "다이어리 상세 조회 성공: $response")
                val diaryDetail = convertToDiaryDetail(response)
                _diarySelectState.value = DiarySelectUiState.Success(diaryDetail)
            }.onFailure { error ->
                Log.e("DiarySelectViewModel", "다이어리 상세 조회 실패: ${error.message}")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorDiaryDetail())
            }
        }
    }

    fun selectQuestion(id: String) {
        viewModelScope.launch {
            _diarySelectState.value = DiarySelectUiState.Loading
            delay(200) // 실제 Repository 연동 시 제거
            
            val item = questionStore[id]
            if (item != null) {
                _diarySelectState.value = DiarySelectUiState.Success(item)
            } else {
                // 에러 시 기본 데이터 반환
                _diarySelectState.value = DiarySelectUiState.Success(questionStore["1"]!!)
            }
        }
    }

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            val id = diaryId.toLongOrNull()
            if (id == null) {
                Log.e("DiarySelectViewModel", "잘못된 다이어리 ID: $diaryId")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorDiaryDetail())
                return@launch
            }

            val result = diaryRepository.deleteDiary(id)
            result.onSuccess { message ->
                Log.d("DiarySelectViewModel", "다이어리 삭제 성공: $message")
                _diarySelectState.value = DiarySelectUiState.Deleted
            }.onFailure { error ->
                Log.e("DiarySelectViewModel", "다이어리 삭제 실패: ${error.message}")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorDiaryDetail())
            }
        }
    }

    private fun convertToDiaryDetail(response: DiaryDetailData): DiaryDetail {
        return DiaryDetail(
            id = response.id.toString(),
            date = formatDate(response.diaryAt),
            diaryText = response.content,
            emotions = response.emotions.map { emotion ->
                EmotionResult(
                    emotion = emotion.label,
                    percentage = (emotion.score * 100).toFloat(),
                    color = mapEmotionToColor(emotion.label)
                )
            },
            aiFeedback = response.feedBack
        )
    }

    private fun formatDate(dateTimeString: String): String {
        return try {
            val dateTime = LocalDateTime.parse(dateTimeString.take(19))
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
            dateTime.format(formatter)
        } catch (e: Exception) {
            Log.e("DiarySelectViewModel", "날짜 파싱 실패: '$dateTimeString'")
            dateTimeString.take(10).replace("-", ".")
        }
    }

    private fun mapEmotionToColor(emotionLabel: String): androidx.compose.ui.graphics.Color {
        return when (emotionLabel) {
            "행복" -> EmotionColorConstants.HAPPINESS
            "혐오" -> EmotionColorConstants.DISGUST
            "놀람" -> EmotionColorConstants.SURPRISE
            "슬픔" -> EmotionColorConstants.SADNESS
            "분노" -> EmotionColorConstants.ANGER
            "불안" -> EmotionColorConstants.ANXIETY
            else -> EmotionColorConstants.HAPPINESS
        }
    }

    private fun getErrorDiaryDetail(): DiaryDetail {
        return DiaryDetail(
            id = "error",
            date = "오류 발생",
            diaryText = "다이어리 정보를 불러오는 중 오류가 발생했습니다.",
            emotions = emptyList(),
            aiFeedback = "다이어리 정보를 불러올 수 없습니다."
        )
    }

    // 테스트 데이터 (임시)
    // TODO: 공통질문 api 연동 시 제거
    private val diaryStore: MutableMap<String, DiaryDetail> = mutableMapOf(
        "1" to DiaryDetail(
            id = "1",
            date = "2025.08.01(금)",
            diaryText = "오늘은 1인당 26만원인 한국에서 제일 비싼 뷔페를 다녀왔어요.",
            emotions = listOf(
                EmotionResult("행복", 0.45f, EmotionColorConstants.HAPPINESS),
                EmotionResult("혐오", 0.21f, EmotionColorConstants.DISGUST),
                EmotionResult("놀람", 0.18f, EmotionColorConstants.SURPRISE),
                EmotionResult("슬픔", 0.09f, EmotionColorConstants.SADNESS),
                EmotionResult("분노", 0.05f, EmotionColorConstants.ANGER),
                EmotionResult("불안", 0.02f, EmotionColorConstants.ANXIETY)
            ),
            aiFeedback = "AI가 작성한 피드백입니다. 반갑습니다. 안녕하세요. 어서오세요. 하이루"
        )
    )

    private val questionStore: MutableMap<String, QuestionDetail> = mutableMapOf(
        "1" to QuestionDetail(
            id = "1",
            date = "2025.08.01(금)",
            items = listOf(
                QuestionWithAnswers(
                    question = Question(
                        id = "q1",
                        date = "2025.08.01(금)",
                        question = "오늘 누군가에게 고마움을 느꼈다면 누구인가요?",
                        responders = roleLabelsToResponders(listOf("아빠", "엄마", "첫째 아들", "둘째 딸"))
                    ),
                    answers = listOf(
                        "아빠" to "오늘 아침에 일찍 일어나서 차를 준비해줘서 감사했어요.",
                        "엄마" to "아침 식사를 맛있게 먹어줘서 고마웠어요.",
                        "첫째 아들" to "동생을 잘 챙겨주어서 고마웠어요.",
                        "둘째 딸" to "오늘 학교에서 도움이 필요할 때 오빠가 도와줘서 고마웠어요."
                    )
                ),
                QuestionWithAnswers(
                    question = Question(
                        id = "q2",
                        date = "2025.08.01(금)",
                        question = "오늘 가장 기억에 남는 순간은 무엇인가요?",
                        responders = roleLabelsToResponders(listOf("할아버지", "할머니"))
                    ),
                    answers = listOf(
                        "할아버지" to "가족들과 함께 저녁 식사를 하면서 이야기하는 시간이 가장 기억에 남아요.",
                        "할머니" to "손자들이 학교에서 돌아와서 하루 일과를 들려주는 순간이 가장 소중했어요."
                    )
                ),
                QuestionWithAnswers(
                    question = Question(
                        id = "q3",
                        date = "2025.08.01(금)",
                        question = "내일의 나에게 한마디를 적어볼까요?",
                        responders = roleLabelsToResponders(listOf("첫째 아들", "둘째 딸"))
                    ),
                    answers = listOf(
                        "첫째 아들" to "내일도 오늘처럼 가족들과 함께하는 시간을 소중히 여기고, 작은 일에도 감사함을 느끼며 살아가자.",
                        "둘째 딸" to "매일 새로운 기회가 있다는 것을 기억하고, 어려운 일이 있어도 포기하지 말고 노력하자."
                    )
                )
            )
        )
    )
}


