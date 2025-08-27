package com.example.capstone_404.feature.album.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Photo(
    val id: String,
    val title: String,
    val thumbnailUrl: String,
    val imageUrl: String,
    val content: String,
    val area: String,
    val date: String,
    val time: String,
    val userIds: List<Int>
) {

    val sortableDateTime: LocalDateTime
        get() {
        return try {
            val timeValue = if (time.isNotEmpty()) time else "00:00"
            LocalDateTime.parse("${date}T$timeValue:00")
        } catch (e: Exception) {
            try {
                LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)
            } catch (e: Exception) {
                LocalDateTime.MIN
            }
        }
    }
}