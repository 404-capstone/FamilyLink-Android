package com.example.capstone_404.feature.album.model

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object DateTimeUtil {
    private val fullDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MM.dd(E)", Locale.KOREA)
    private val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)

    // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환
    @SuppressLint("DefaultLocale")
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
    @SuppressLint("DefaultLocale")
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance()
        return String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }

    //날짜와 시간을 "2025.01.01(수) 오후 02:30" 형식으로 포맷, 시간이 비어있으면 날짜만 반환
    fun formatDateTime(date: String, time: String): String {
        return try {
            val localDate = LocalDate.parse(date)
            val formattedDate = fullDateFormatter.format(localDate)

            if (time.isNotEmpty()) {
                val localTime = LocalTime.parse(time)
                val formattedTime = timeFormatter.format(localTime)
                "$formattedDate $formattedTime"
            } else {
                formattedDate
            }
        } catch (e: Exception) {
            if (time.isNotEmpty()) "$date $time" else date
        }
    }


    // 날짜를 "01.01(수)" 형식으로 포맷 (짧은 형식)
    fun formatDateForDisplay(date: String): String {
        return try {
            val localDate = LocalDate.parse(date)
            shortDateFormatter.format(localDate)
        } catch (e: Exception) {
            val parts = date.split("-")
            if (parts.size == 3) {
                "${parts[1]}월 ${parts[2]}일"
            } else date
        }
    }

    fun normalizeTimeForApi(time: String): String {
        if (time.isBlank()) return time
        return try {
            val hasAmPm = time.startsWith("오전") || time.startsWith("오후")
            if (hasAmPm) {
                val t = LocalTime.parse(time, DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA))
                t.format(DateTimeFormatter.ofPattern("HH:mm"))
            } else time
        } catch (e: Exception) {
            time
        }
    }

    fun formatTimeForDisplay(time: String): String {
        if (time.isBlank()) return time
        return try {
            val hasAmPm = time.startsWith("오전") || time.startsWith("오후")
            if (hasAmPm) {
                time
            } else {
                val t = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
                t.format(DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA))
            }
        } catch (e: Exception) {
            time
        }
    }
}