package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 그룹 생성 Response
data class GroupData(
    val groupName: String,
    val groupId: Int
)

// 그룹 ID 조회 Response - BaseResponse 사용

// 그룹 정보 조회 Response - 그룹 정보
@Serializable
data class GroupInfoData(
    val group_id: Int,
    val group_name: String,
    val group_image: String? = null,
    val userinfo: List<GroupUserInfoData>
)
// 그룹 정보 조회 Response - 그룹원 정보
@Serializable
data class GroupUserInfoData(
    val userId: Int,
    val username: String,
    val role: String,
    val age: String? = null,
    val image: String? = null,
    val leader: Boolean
)

// 설문 결과 저장 Response - BaseResponse 사용

// 설문 결과 조회 Response
@Serializable
data class SurveyResultData(
    val level: String,
    val score: Int,
    val percent: Int
)