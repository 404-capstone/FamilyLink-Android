package com.example.capstone_404.feature.diary.model

// 다이어리 상세 정보 모델
data class DiaryDetail(
    val id: String,
    val date: String,
    val diaryText: String,
    val emotions: List<EmotionResult>,
    val aiFeedback: String
)


// 질문과 답변을 묶은 모델
data class QuestionWithAnswers(
    val question: Question,
    val answers: List<Pair<String, String>>
)


//공통 질문 상세 정보 모델
data class QuestionDetail(
    val id: String,
    val date: String,
    val items: List<QuestionWithAnswers>
)


// 다이어리/질문 선택 화면의 UI 상태
sealed class DiarySelectUiState {
    data object Loading : DiarySelectUiState()
    data object Deleted : DiarySelectUiState()
    data class Success(val detail: Any) : DiarySelectUiState()
}
