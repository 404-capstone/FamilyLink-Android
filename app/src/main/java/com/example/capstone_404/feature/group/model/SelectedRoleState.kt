package com.example.capstone_404.feature.group.model

// 선택된 역할 정보 데이터 클래스
data class SelectedRoleState(
    val role: RoleType? = null,
    val order: OrderType? = null
)