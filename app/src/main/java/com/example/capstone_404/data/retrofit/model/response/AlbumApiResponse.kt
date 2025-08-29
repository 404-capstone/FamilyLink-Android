package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 앨범 검색 응답 데이터
@Serializable
data class AlbumSearchData(
    val groupId: Int,
    val album: List<AlbumInfoData>?
)

// 앨범 정보 데이터
@Serializable
data class AlbumInfoData(
    val date: String,  // (YYYY-MM)
    val photo: List<PhotoInfoData>
)

// 사진 정보 response
@Serializable
data class PhotoInfoData(
    val photoid: Int,
    val title: String,
    val thumnailurl: String,
    val content: String?,
    val area: String?,
    val date: String,
    val time: String?,
    val userid: List<Int>?
)

// 사진 저장 응답 데이터
@Serializable
data class AlbumSaveResponse(
    val albumId: Int,
    val photoId: Int,
    val size: Int
)
