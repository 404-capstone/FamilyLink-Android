package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.GroupApi
import com.example.capstone_404.data.retrofit.model.response.GroupData
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val groupApi: GroupApi
) : GroupRepository {

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
}