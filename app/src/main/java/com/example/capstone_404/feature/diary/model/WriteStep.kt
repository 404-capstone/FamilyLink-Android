package com.example.capstone_404.feature.diary.model

// 다이어리 작성 단계
enum class WriteStep {
    DIARY,          // 다이어리 작성
    QUESTION,       // 공통 질문 작성
    LOADING,        // 피드백 생성 로딩
    RESULT          // 피드백 결과
}