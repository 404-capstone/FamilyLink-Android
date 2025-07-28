package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.GroupData
import okhttp3.MultipartBody

// 그룹 관련 API Repository 인터페이스
interface GroupRepository {

    // 그룹 생성
    suspend fun createGroup(
        groupName: String,
        role: String,
        image: MultipartBody.Part? = null
    ): Result<GroupData>
}