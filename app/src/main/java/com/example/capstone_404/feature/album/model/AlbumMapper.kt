package com.example.capstone_404.feature.album.model

import com.example.capstone_404.data.retrofit.model.response.AlbumInfoData
import com.example.capstone_404.data.retrofit.model.response.PhotoInfoData

// API Response를 UI 모델로 변환
fun List<AlbumInfoData>.toAlbumMap(): Map<String, List<Photo>> {
    return mapNotNull { albumInfo ->
        val photos = albumInfo.photoInfoDtoList.map { it.toPhoto() }
        if (photos.isNotEmpty()) {
            albumInfo.date to photos
        } else {
            null // 빈 앨범은 제외
        }
    }.toMap()
}

// PhotoInfoData를 Photo로 변환
fun PhotoInfoData.toPhoto(): Photo {
    return Photo(
        id = photoid,
        title = title,
        thumbnailUrl = thumbnailurl,
        content = content,
        area = area,
        time = time,
        userIds = userid
    )
}

// 날짜 포맷 변환 (2025-04 -> 2025년 4월)
fun String.toKoreanYearMonth(): String {
    val parts = split("-")
    return if (parts.size == 2) {
        "${parts[0]}년 ${parts[1].toIntOrNull() ?: parts[1]}월"
    } else this
}

// 날짜 포맷 검증
fun String.isValidYearMonth(): Boolean {
    val regex = Regex("\\d{4}-\\d{2}")
    return matches(regex)
}
