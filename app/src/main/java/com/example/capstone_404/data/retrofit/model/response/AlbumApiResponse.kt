package com.example.capstone_404.data.retrofit.model.response

import kotlinx.serialization.Serializable

// 앨범 검색 응답 데이터
@Serializable
data class AlbumSearchData(
    val groupId: Int,
    val albumInfoDtoList: List<AlbumInfoData>
)

// 앨범 정보 데이터
@Serializable
data class AlbumInfoData(
    val date: String,  // (YYYY-MM)
    val photoInfoDtoList: List<PhotoInfoData>
)

// 사진 정보 response
@Serializable
data class PhotoInfoData(
    val photoid: Int,
    val title: String,
    val thumbnailurl: String,
    val content: String,
    val area: String,
    val time: Long,
    val userid: List<Int>
)
