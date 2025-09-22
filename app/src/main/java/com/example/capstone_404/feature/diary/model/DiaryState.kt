package com.example.capstone_404.feature.diary.model

// 다이어리 상태 관리
sealed class DiaryState {
    data object Loading : DiaryState()
    data class Success(
        val diaryEntries: List<DiaryEntry>,
        val questions: List<Question>
    ) : DiaryState()
}
