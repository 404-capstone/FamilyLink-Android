package com.example.capstone_404.feature.diary.model

// 다이어리 작성 상태
data class DiaryWriteState(
    val step: WriteStep = WriteStep.DIARY,
    val diaryText: String = "",
    val questionTexts: List<String> = emptyList(),
    val answerTexts: List<String> = emptyList(),
    val isDiaryEmpty: Boolean = true,
    val isDiaryTooShort: Boolean = false,
    val result: FeedbackResult? = null
)