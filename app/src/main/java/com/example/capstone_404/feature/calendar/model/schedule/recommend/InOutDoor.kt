package com.example.capstone_404.feature.calendar.model.schedule.recommend

enum class InOutDoor { INDOOR, OUTDOOR, ALL }

fun InOutDoor?.toKorean(): String = when (this) {
    InOutDoor.INDOOR -> "실내"
    InOutDoor.OUTDOOR -> "실외"
    null, InOutDoor.ALL -> "실내/실외 무관"
}