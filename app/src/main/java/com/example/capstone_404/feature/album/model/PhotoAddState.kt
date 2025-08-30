package com.example.capstone_404.feature.album.model

import android.net.Uri

data class PhotoAddState(
    val selectedImage: Uri? = null,
    val title: String = "",
    val date: String = DateTimeUtil.getCurrentDate(),  // 기본값: 현재 날짜 (필수)
    val time: String = "",                             // 기본값: 빈 값 (선택사항)
    val location: String = "",
    val description: String = "",
    val selectedParticipants: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)