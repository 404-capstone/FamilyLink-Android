package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData

// 앨범 관련 API Repository 인터페이스
interface AlbumRepository {

    // 사진 전체 조회
    suspend fun getAllAlbums(
        groupId: Int
    ): Result<AlbumSearchData>
}
