package com.example.capstone_404.utils

object AgeConverter {
    //나이를 연령대로 변환하는 함수
    fun convertAgeToRange(age: Int): String? {
        return when (age) {
            0 -> "10대 미만"
            10 -> "10대"
            20 -> "20대"
            30 -> "30대"
            40 -> "40대"
            50 -> "50대"
            60 -> "60대"
            70 -> "70대"
            80 -> "80대"
            90 -> "90대 이상"
            else -> null
        }
    }

    //연령대를 나이로 변환하는 함수
    fun convertRangeToAge(ageRange: String): Int? {
        return when (ageRange) {
            "10대 미만" -> 0
            "10대" -> 10
            "20대" -> 20
            "30대" -> 30
            "40대" -> 40
            "50대" -> 50
            "60대" -> 60
            "70대" -> 70
            "80대" -> 80
            "90대 이상" -> 90
            else -> null
        }
    }
}