package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.GroupData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import okhttp3.MultipartBody

// 그룹 관련 API Repository 인터페이스
interface GroupRepository {

    // 그룹 생성
    suspend fun createGroup(
        groupName: String,
        role: String,
        image: MultipartBody.Part
    ): Result<GroupData>

    // 그룹 ID 조회
    suspend fun getGroupIdFromServer(
    ): Result<Int>

    // 그룹 정보 조회
    suspend fun getGroupInfo(
        groupId: Int
    ): Result<GroupInfoData>

    // 설문 결과 저장
    suspend fun saveSurveyResult(
        groupId: Int,
        level: String,
        score: Int,
        percent: Int
    ): Result<String>
}