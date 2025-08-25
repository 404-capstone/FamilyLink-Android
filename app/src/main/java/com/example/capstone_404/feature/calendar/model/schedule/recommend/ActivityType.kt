package com.example.capstone_404.feature.calendar.model.schedule.recommend

import com.example.capstone_404.R

enum class ActivityType(val label: String) {
    EAT("식사"),
    FOOD_DRINK("먹거리/음료"),
    HEALING_RELAX("힐링/휴식"),
    SPORTS_LEISURE("스포츠/레저"),
    CREATIVE_EXPERIENCE("창의/체험"),
    CULTURE_ART("문화/예술"),
    TRAVEL_TOUR("여행/탐방")
}

// 아이콘 매핑
fun ActivityType.iconRes(): Int = when (this) {
    ActivityType.EAT -> R.drawable.ic_eat
    ActivityType.FOOD_DRINK -> R.drawable.ic_drink
    ActivityType.HEALING_RELAX -> R.drawable.ic_healing
    ActivityType.SPORTS_LEISURE -> R.drawable.ic_sport
    ActivityType.CREATIVE_EXPERIENCE -> R.drawable.ic_puzzle
    ActivityType.CULTURE_ART -> R.drawable.ic_art
    ActivityType.TRAVEL_TOUR -> R.drawable.ic_travel
}