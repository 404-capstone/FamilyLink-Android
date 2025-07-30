package com.example.capstone_404.feature.group.model

import androidx.compose.ui.graphics.Color

// UI 테스트용 임시 데이터
data class GroupUserUiModel(
    val userId: Int,
    val username: String,
    val age: String,
    val role: String,
    val imageUrl: String,
    val isLeader: Boolean,
    val roleSplit: Pair<RoleType, OrderType?>,
    val roleColor: Color
)

data class GroupInfoUiState(
    val groupId: Int,
    val groupName: String,
    val groupImageUrl: String,
    val users: List<GroupUserUiModel>
)

val roleSplit1: Pair<RoleType, OrderType?> = parseRoleAndOrder("첫째 아들")
val roleSplit2: Pair<RoleType, OrderType?> = parseRoleAndOrder("둘째 아들")
val roleSplit3: Pair<RoleType, OrderType?> = parseRoleAndOrder("셋째 딸")

val GroupInfoSample: GroupInfoUiState
    get() = GroupInfoUiState(1,"Test","", GroupInfoList)
val GroupInfoList = listOf (
    GroupUserUiModel(1, "홍길동", "20대", "첫째 아들", "", true, parseRoleAndOrder("첫째 아들"), roleSplit1.first.getColor(roleSplit1.second) ),
    GroupUserUiModel(2, "홍길돤", "20대", "둘째 아들", "", false, parseRoleAndOrder("첫째 아들"), roleSplit2.first.getColor(roleSplit2.second) ),
    GroupUserUiModel(3, "홍길순", "10대", "셋째 딸", "", false, parseRoleAndOrder("첫째 아들"), roleSplit3.first.getColor(roleSplit3.second) )
)