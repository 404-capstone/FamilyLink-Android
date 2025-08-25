package com.example.capstone_404.feature.calendar.model.schedule.recommend

import com.example.capstone_404.data.retrofit.model.response.RecommendData

sealed interface RecommendResultUiState {
    data object Idle : RecommendResultUiState
    data object Loading : RecommendResultUiState
    data class Success(val data: RecommendData) : RecommendResultUiState
    data class Error(val message: String) : RecommendResultUiState
}