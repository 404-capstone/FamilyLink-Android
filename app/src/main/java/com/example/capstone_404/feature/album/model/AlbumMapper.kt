package com.example.capstone_404.feature.album.model

import com.example.capstone_404.data.retrofit.model.response.AlbumInfoData
import com.example.capstone_404.data.retrofit.model.response.PhotoInfoData

fun List<AlbumInfoData>.toAlbumMap(): Map<String, List<Photo>> {
    return mapNotNull { albumInfo ->
        val photos = albumInfo.photoInfoDtoList.map { it.toPhoto() }
        if (photos.isNotEmpty()) {
            albumInfo.date to photos
        } else {
            null
        }
    }.toMap()
}

fun PhotoInfoData.toPhoto(): Photo {
    return Photo(
        id = photoid.toString(),
        title = title,
        thumbnailUrl = thumbnailurl,
        imageUrl = thumbnailurl,
        content = content,
        area = area,
        date = date,
        time = time,
        userIds = userid
    )
}


fun String.toKoreanYearMonth(): String {
    val parts = split("-")
    return if (parts.size == 2) {
        "${parts[0]}년 ${parts[1].toIntOrNull() ?: parts[1]}월"
    } else this
}


fun String.isValidYearMonth(): Boolean {
    val regex = Regex("\\d{4}-\\d{2}")
    return matches(regex)
}
