package com.example.capstone_404.data.retrofit.api

import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData
import com.example.capstone_404.data.retrofit.model.response.BaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// 앨범 관련 API 인터페이스
interface AlbumApi {

    // 사진 전체 조회 (토큰 자동 포함)
    @GET("/album/search")
    suspend fun getAllAlbums(
        @Query("groupId") groupId: Int
    ): Response<BaseResponse<AlbumSearchData>>

    // TODO: 추후 추가될 API
    // @POST("/album/add") - 사진 추가
    // @DELETE("/album/delete") - 사진 삭제
    // @PATCH("/album/edit") - 사진 정보 수정
}