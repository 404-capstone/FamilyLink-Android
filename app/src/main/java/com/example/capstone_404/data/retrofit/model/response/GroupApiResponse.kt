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