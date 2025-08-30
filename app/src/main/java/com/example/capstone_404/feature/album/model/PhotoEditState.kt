package com.example.capstone_404.feature.album.model

import com.example.capstone_404.data.retrofit.model.request.PhotoEditRequest

data class PhotoEditState(
    val photoId: String = "",
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val description: String = "",
    val selectedParticipants: List<Int> = emptyList()
) {
    // 데이터 변환 함수
    fun toEditRequest(): PhotoEditRequest {
        return PhotoEditRequest(
            photoId = photoId.toIntOrNull() ?: 0,
            title = title,
            area = location.takeIf { it.isNotEmpty() },
            content = description.takeIf { it.isNotEmpty() },
            date = date,
            time = time.takeIf { it.isNotEmpty() },
            userId = selectedParticipants.takeIf { it.isNotEmpty() }
        )
    }
}