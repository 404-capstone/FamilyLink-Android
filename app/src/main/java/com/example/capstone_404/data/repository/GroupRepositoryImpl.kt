package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.GroupApi
import com.example.capstone_404.data.retrofit.model.request.SaveSurveyResultRequest
import com.example.capstone_404.data.retrofit.model.response.GroupData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.data.retrofit.model.response.SurveyResultData
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val groupApi: GroupApi
) : GroupRepository {

    // 그룹 생성
    override suspend fun createGroup(
        groupName: String,
        role: String,
        image: MultipartBody.Part
    ): Result<GroupData> {
        return try {
            val response = groupApi.createGroup(groupName, role, image)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 ID 조회
    override suspend fun getGroupIdFromServer(): Result<Int> {
        return try {
            val response = groupApi.getGroupId()
            if (response.isSuccessful) {
                val groupId = response.body()?.data
                if (groupId != null) {
                    Result.success(groupId)
                } else {
                    Result.failure(Exception("응답 데이터 없음"))
                }
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 정보 조회
    override suspend fun getGroupInfo(groupId: Int): Result<GroupInfoData> {
        return try {
            val response = groupApi.getGroupInfo(groupId)
            if (response.isSuccessful) {
                response.body()?.data?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 정보 수정
    override suspend fun editGroupInfo(
        groupId: Int,
        groupName: String,
        image: MultipartBody.Part
    ): Result<Unit> {
        return try {
            val response = groupApi.editGroupInfo(groupId, groupName, image)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 설문 결과 저장
    override suspend fun saveSurveyResult(
        groupId: Int,
        level: String,
        score: Int,
        percent: Int
    ): Result<String> {
        return try {
            val body = SaveSurveyResultRequest(level = level, score = score, percent = percent)
            val response = groupApi.saveSurveyResult(groupId, body)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 설문 결과 조회
    override suspend fun getSurveyResult(groupId: Int): Result<SurveyResultData> {
        return try {
            val response = groupApi.getSurveyResult(groupId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 초대 코드 조회
    override suspend fun getInviteCode(groupId: Int): Result<String> {
        return try {
            val response = groupApi.getInviteCode(groupId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorData = response.errorBody()?.string()
                Result.failure(Exception("오류 결과: $errorData"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 초대 코드 생성
    override suspend fun createInviteCode(groupId: Int): Result<String> {
        return try {
            val response = groupApi.createInviteCode(groupId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 초대 코드 기반 그룹 ID 조회
    override suspend fun getGroupIdByCode(inviteCode: String): Result<Int> {
        return try {
            val response = groupApi.getGroupIdByCode(inviteCode)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 가입
    override suspend fun joinGroup(
        inviteCode: String,
        role: String
    ): Result<GroupData> {
        return try {
            val response = groupApi.joinGroup(inviteCode, role)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹장 변경
    override suspend fun changeLeader(
        groupId: Int,
        userId: Int
    ): Result<String> {
        return try {
            val response = groupApi.changeLeader(groupId, userId)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹원 추방
    override suspend fun deleteMember(
        groupId: Int,
        userId: Int
    ): Result<Unit> {
        return try {
            val response = groupApi.deleteMember(groupId, userId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 그룹 탈퇴
    override suspend fun exitGroup(groupId: Int): Result<Unit> {
        return try {
            val response = groupApi.exitGroup(groupId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}