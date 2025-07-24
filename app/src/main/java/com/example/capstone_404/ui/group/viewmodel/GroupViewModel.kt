package com.example.capstone_404.ui.group.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.capstone_404.ui.group.model.GroupEntryState
import com.example.capstone_404.ui.group.model.OrderType
import com.example.capstone_404.ui.group.model.RoleType
import com.example.capstone_404.ui.group.model.SelectedRoleState
import com.example.capstone_404.ui.group.model.toKorean
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor() : ViewModel() {

    // 그룹 가입 여부 상태 관리
    var isJoined by mutableStateOf(false)
        private set

    // 역할 선택 페이지 이동 경로(데이터) 관리
    private var groupEntryState by mutableStateOf<GroupEntryState>(GroupEntryState.None)

    // 역할 선택 상태
    var selectedRoleState by mutableStateOf(SelectedRoleState())
        private set

    // 생성하기 경로일 때
    fun setGenerationState(groupName: String, imageUri: Uri?) {
        groupEntryState = GroupEntryState.Generation(groupName, imageUri)
    }
    // 가입하기 경로일 때
    fun setAccessState(inviteCode: String) {
        groupEntryState = GroupEntryState.Access(inviteCode)
    }
    // 초기화
    fun clearState() {
        groupEntryState = GroupEntryState.None
    }

    // 역할 선택 상태 변경
    fun updateSelectedRole(role: RoleType?) {
        selectedRoleState = selectedRoleState.copy(role = role)

        if (role != RoleType.SON && role != RoleType.DAUGHTER) {
            selectedRoleState = selectedRoleState.copy(order = null)
        }
    }

    // 아들&딸 Order 변경
    fun updateSelectedOrder(order: OrderType?) {
        selectedRoleState = selectedRoleState.copy(order = order)
    }

    // 최종 생성 or 가입(임시로 로그만)
    fun groupSubmit() {
        Log.d("GroupViewModel", "전달 데이터: $groupEntryState + ${(selectedRoleState.order?.label ?: "") + selectedRoleState.role?.toKorean()}" )
    }
}