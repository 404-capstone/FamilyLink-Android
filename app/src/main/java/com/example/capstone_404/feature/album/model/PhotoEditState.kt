package com.example.capstone_404.feature.album.model

data class PhotoEditState(
    val photoId: String = "",
    val title: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val description: String = "",
    val selectedParticipants: List<Int> = emptyList()
)