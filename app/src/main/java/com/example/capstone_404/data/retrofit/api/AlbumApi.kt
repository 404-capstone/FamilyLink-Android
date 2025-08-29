package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.AlbumSaveResponse
import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// 앨범 관련 API 인터페이스
interface AlbumApi {

    // 사진 전체 조회 (토큰 자동 포함)
    @GET("/album/search")
    suspend fun getAllAlbums(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<AlbumSearchData>>

    // 사진 저장
    @Multipart
    @POST("/album/add")
    suspend fun addPhoto(
        @Query("groupId") groupId: Int,
        @Query("title") title: String,
        @Query("date") date: String,
        @Query("time") time: String?,
        @Query("content") content: String?,
        @Query("area") area: String?,
        @Query("userId") userIds: Array<Int>?,
        @Part image: List<MultipartBody.Part>
    ): Response<BaseResponse<AlbumSaveResponse>>

    // TODO: 추후 추가될 API
    // @DELETE("/album/delete") - 사진 삭제
    // @PATCH("/album/edit") - 사진 정보 수정
}