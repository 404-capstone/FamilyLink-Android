package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.GroupCreateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// 그룹 관련 API 인터페이스
interface GroupApi {

    // 그룹 생성
    @Multipart
    @POST("/group/generation")
    suspend fun createGroup(
        @Query("groupname") groupName: String,
        @Query("role") role: String,
        @Part image: MultipartBody.Part?
    ): Response<GroupCreateResponse>
}