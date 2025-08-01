package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.request.SaveSurveyResultRequest
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import com.example.capstone_404.data.retrofit.model.response.GroupData
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.data.retrofit.model.response.SurveyResultData
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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
        @Part image: MultipartBody.Part
    ): Response<BaseResponse<GroupData>>

    // 그룹 ID 조회
    @GET("/group/id/search")
    suspend fun getGroupId(
    ): Response<BaseResponse<Int>>

    // 그룹 정보 조회
    @GET("/group/search")
    suspend fun getGroupInfo(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<GroupInfoData>>

    // 설문 결과 저장
    @POST("/group/servey/save")
    suspend fun saveSurveyResult(
        @Query("groupId") groupId: Int,
        @Body body: SaveSurveyResultRequest
    ): Response<BaseResponse<String>>

    // 설문 결과 조회
    @GET("/group/servey/search")
    suspend fun getSurveyResult(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<SurveyResultData>>

    // 초대 코드 조회
    @GET("/group/code/search")
    suspend fun getInviteCode(
        @Query("groupid") groupId: Int
    ): Response<BaseResponse<String>>
}