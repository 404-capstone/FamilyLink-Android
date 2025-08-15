package com.example.capstone_404.feature.calendar.model

import java.time.format.DateTimeFormatter
import java.util.Locale

val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)

// 서버 맞춤 시간 포맷
val serverFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")