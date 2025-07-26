package com.example.capstone_404.feature.group.model

import android.net.Uri

// 역할 선택 페이지 이동 경로(데이터) 분류
sealed class GroupEntryState {
    data class Generation(val groupName: String, val imageUri: Uri?) : GroupEntryState()
    data class Access(val inviteCode: String) : GroupEntryState()
    data object None : GroupEntryState()
}