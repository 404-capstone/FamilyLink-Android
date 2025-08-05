package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.GroupData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.data.retrofit.model.response.SurveyResultData
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

    // 그룹 정보 수정
    suspend fun editGroupInfo(
        groupId: Int,
        groupName: String,
        image: MultipartBody.Part
    ): Result<Unit>

    // 설문 결과 저장
    suspend fun saveSurveyResult(
        groupId: Int,
        level: String,
        score: Int,
        percent: Int
    ): Result<String>

    // 설문 결과 조회
    suspend fun getSurveyResult(
        groupId: Int
    ): Result<SurveyResultData>

    // 초대 코드 조회
    suspend fun getInviteCode(
        groupId: Int
    ): Result<String>

    // 초대 코드 생성
    suspend fun createInviteCode(
        groupId: Int
    ): Result<String>

    // 초대 코드 기반 그룹 ID 조회
    suspend fun getGroupIdByCode(
        inviteCode: String
    ): Result<Int>

    // 그룹 가입
    suspend fun joinGroup(
        inviteCode: String,
        role: String
    ): Result<GroupData>

    // 그룹장 변경
    suspend fun changeLeader(
        groupId: Int,
        userId: Int
    ): Result<String>

    // 그룹원 추방
    suspend fun deleteMember(
        groupId: Int,
        userId: Int
    ): Result<Unit>

    // 그룹 탈퇴
    suspend fun exitGroup(
        groupId: Int
    ): Result<Unit>

    // 그룹장 그룹 탈퇴
    suspend fun exitGroupFromLeader(
        groupId: Int,
        targetId: Int
    ): Result<String>
}