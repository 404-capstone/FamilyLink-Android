package com.example.capstone_404.feature.diary.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.DiaryRepository
import com.example.capstone_404.data.retrofit.model.response.DiaryDetailData
import com.example.capstone_404.data.retrofit.model.response.GroupAnswerDetailData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.feature.diary.model.DiaryDetail
import com.example.capstone_404.feature.diary.model.QuestionDetail
import com.example.capstone_404.feature.diary.model.DiarySelectUiState
import com.example.capstone_404.feature.diary.model.EmotionResult
import com.example.capstone_404.feature.diary.model.EmotionColorConstants
import com.example.capstone_404.feature.diary.model.FamilyEmotion
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.QuestionWithAnswers
import com.example.capstone_404.feature.diary.model.roleLabelsToResponders
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiarySelectViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userInfoManager: UserInfoManager,
    groupInfoManager: GroupInfoManager
) : ViewModel() {

    // 역할 변환용 그룹 정보 Flow
    private val groupInfoFlow: Flow<GroupInfoData?> = groupInfoManager.groupInfoFlow

    // 상태 관리
    private val _diarySelectState = MutableStateFlow<DiarySelectUiState>(DiarySelectUiState.Loading)
    val diarySelectState: StateFlow<DiarySelectUiState> = _diarySelectState.asStateFlow()

    // 비즈니스 로직
    fun selectDiary(id: String) {
        viewModelScope.launch {
            _diarySelectState.value = DiarySelectUiState.Loading

            val diaryId = id.toIntOrNull()
            if (diaryId == null) {
                Log.e("DiarySelectViewModel", "잘못된 다이어리 ID: $id")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorDiaryDetail())
                return@launch
            }

            val result = diaryRepository.getDiaryDetail(diaryId)
            result.onSuccess { response ->
                Log.d("DiarySelectViewModel", "다이어리 상세 조회 성공: $response")
                // groupInfoFlow에서 그룹 멤버 정보 가져오기
                val groupInfo = groupInfoFlow.first()
                val groupMembers = groupInfo?.userinfo?.map { it.userId to it.role } ?: emptyList()

                val diaryDetail = convertToDiaryDetail(response, groupMembers)
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

            val groupQuestionId = id.toIntOrNull()
            val groupId = userInfoManager.getGroupId()

            if (groupQuestionId == null || groupId == null) {
                Log.e("DiarySelectViewModel", "잘못된 파라미터: questionId=$id, groupId=$groupId")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorQuestionDetail())
                return@launch
            }

            val result = diaryRepository.getQuestionAnswerDetail(groupId, groupQuestionId)
            result.onSuccess { response ->
                Log.d("DiarySelectViewModel", "공통질문 상세 조회 성공: $response")
                val questionDetail = convertToQuestionDetail(response)
                _diarySelectState.value = DiarySelectUiState.Success(questionDetail)
            }.onFailure { error ->
                Log.e("DiarySelectViewModel", "공통질문 상세 조회 실패: ${error.message}")
                _diarySelectState.value = DiarySelectUiState.Success(getErrorQuestionDetail())
            }
        }
    }

    fun deleteDiary(diaryId: String) {
        viewModelScope.launch {
            val id = diaryId.toIntOrNull()
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

    private fun convertToDiaryDetail(
        response: DiaryDetailData,
        groupMembers: List<Pair<Int, String>> = emptyList()
    ): DiaryDetail {
        return DiaryDetail(
            id = response.id.toString(),
            date = formatDate(response.diaryAt),
            diaryText = response.content,
            emotions = response.emotions.map { emotion ->
                EmotionResult(
                    emotion = emotion.label,
                    percentage = (emotion.score / 100f).toFloat(),
                    color = mapEmotionToColor(emotion.label)
                )
            },
            aiFeedback = response.feedBack,
            familyEmotions = response.familyEmotion.mapNotNull { emotionData ->
                val roleLabel = groupMembers.find { it.first == emotionData.userId }?.second

                if (roleLabel != null) {
                    val (roleType, orderType) = parseRoleAndOrder(roleLabel)

                    FamilyEmotion(
                        userId = emotionData.userId,
                        roleLabel = roleLabel,
                        emotion = emotionData.emotion,
                        color = roleType.getColor(orderType)
                    )
                } else {
                    // 역할을 찾을 수 없으면 제외
                    Log.w("DiarySelectViewModel", "userId=${emotionData.userId}에 해당하는 역할 없음")
                    null
                }
            }
        )
    }

    private fun convertToQuestionDetail(response: GroupAnswerDetailData): QuestionDetail {
        return QuestionDetail(
            id = response.groupQuestionId.toString(),
            date = formatDate(response.date),
            items = response.questionInfo.map { questionInfo ->
                QuestionWithAnswers(
                    question = Question(
                        id = questionInfo.questionId.toString(),
                        date = formatDate(response.date),
                        question = questionInfo.question,
                        responders = roleLabelsToResponders(
                            questionInfo.answerInfo.map { it.postion }
                        )
                    ),
                    answers = questionInfo.answerInfo.map { answer ->
                        answer.postion to (answer.answer ?: "응답하지 않았습니다.")
                    }
                )
            }
        )
    }

    private fun formatDate(dateString: String): String {
        return try {
            if (dateString.length > 10) {
                // DateTime 형식 (다이어리)
                val dateTime = LocalDateTime.parse(dateString.take(19))
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
                dateTime.format(formatter)
            } else {
                // Date 형식 (공통질문)
                val date = java.time.LocalDate.parse(dateString)
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
                date.format(formatter)
            }
        } catch (e: Exception) {
            Log.e("DiarySelectViewModel", "날짜 파싱 실패: '$dateString'")
            dateString.take(10).replace("-", ".")
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
            date = "오류가 발생했습니다.",
            diaryText = "다이어리 정보를 불러오는 중 오류가 발생했습니다.\n오류가 계속된다면 관리자에게 문의하세요.",
            emotions = emptyList(),
            aiFeedback = "AI 피드백 정보를 불러올 수 없습니다.",
            familyEmotions = null
        )
    }

    private fun getErrorQuestionDetail(): QuestionDetail {
        return QuestionDetail(
            id = "error",
            date = "오류 발생",
            items = listOf(
                QuestionWithAnswers(
                    question = Question(
                        id = "error",
                        date = "오류 발생",
                        question = "공통질문 정보를 불러오는 중 오류가 발생했습니다.",
                        responders = emptyList()
                    ),
                    answers = emptyList()
                )
            )
        )
    }
}


