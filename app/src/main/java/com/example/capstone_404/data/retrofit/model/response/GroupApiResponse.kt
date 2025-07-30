package com.example.capstone_404.data.retrofit.model.response

// 그룹 생성 Response
data class GroupCreateResponse(
    val code: Int,
    val message: String,
    val data: GroupData
)
data class GroupData(
    val groupName: String,
    val groupId: Int
)

// 그룹 ID 조회 Response
data class GroupIdResponse(
    val code: Int,
    val message: String,
    val data: Int
)