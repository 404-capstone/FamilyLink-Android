package com.example.capstone_404.ui.group.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor() : ViewModel() {

    // 그룹 가입 여부 상태 관리
    var isJoined by mutableStateOf(false)
        private set

}