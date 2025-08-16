package com.example.capstone_404.feature.diary.model

// 다이어리 작성 상태
data class DiaryWriteState(
    val step: WriteStep = WriteStep.DIARY,
    val diaryText: String = "",
    val questionTexts: List<String> = listOf(
        "오늘 가장 기억에 남는 순간은 무엇인가요?",
        "오늘의 감정 변화를 설명해본다면?",
        "내일의 나에게 한마디를 적어볼까요?"
    ),
    val answerTexts: List<String> = listOf("", "", ""),
    val isDiaryEmpty: Boolean = true,
    val isDiaryTooShort: Boolean = false,
    val result: FeedbackResult? = null
)