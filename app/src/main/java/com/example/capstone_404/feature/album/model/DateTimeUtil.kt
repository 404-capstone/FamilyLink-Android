package com.example.capstone_404.feature.album.model

import java.util.Calendar

object DateTimeUtil {
    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%04d-%02d-%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    // 현재 시간을 "HH:mm" 형식으로 반환
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }
}