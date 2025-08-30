package com.example.capstone_404.data.repository

import android.net.Uri
import com.example.capstone_404.data.retrofit.model.request.PhotoEditRequest
import com.example.capstone_404.data.retrofit.model.response.AlbumEditResponse
import com.example.capstone_404.data.retrofit.model.response.AlbumSaveResponse
import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData
import com.example.capstone_404.feature.album.model.PhotoAddState

// 앨범 관련 API Repository 인터페이스
interface AlbumRepository {

    // 사진 전체 조회
    suspend fun getAllAlbums(
        groupId: Int
    ): Result<AlbumSearchData>

    // 사진 저장
    suspend fun addPhoto(
        groupId: Int,
        photoData: PhotoAddState,
        imageUri: Uri
    ): Result<AlbumSaveResponse>

    // 사진 삭제
    suspend fun deletePhoto(
        groupId: Int,
        photoId: String
    ): Result<String>

    // 사진 수정
    suspend fun editPhoto(
        groupId: Int,
        photoEditRequest: PhotoEditRequest
    ): Result<AlbumEditResponse>
}
