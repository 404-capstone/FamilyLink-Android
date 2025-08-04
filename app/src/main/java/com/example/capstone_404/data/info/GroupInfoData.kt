package com.example.capstone_404.data.info

import kotlinx.serialization.Serializable

// 그룹 정보 데이터
@Serializable
data class GroupInfo(
    val groupName: String,
    val groupImage:  String,
    val userinfo: List<GroupUserInfo>
)
// 그룹원 데이터
@Serializable
data class GroupUserInfo(
    val userId: Int,
    val username: String,
    val role:  String,
    val age:  String,
    val image:  String,
    val leader: Boolean
)

// 설문 결과 데이터
@Serializable
data class SurveyResult(
    val level: String? = null,
    val score: Int? = null,
    val percent: Int? = null
)