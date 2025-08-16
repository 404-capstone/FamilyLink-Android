package com.example.capstone_404.feature.diary.model

// 다이어리 상태 관리
sealed class DiaryState {
    data object Loading : DiaryState()
    data object NotJoined : DiaryState()               // 그룹 미가입 상태
    data class Success(
        val diaryEntries: List<DiaryEntry>,
        val questions: List<Question>
    ) : DiaryState()
}
