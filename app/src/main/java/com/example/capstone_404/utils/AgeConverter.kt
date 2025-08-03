package com.example.capstone_404.utils



object AgeConverter {
    //나이를 연령대로 변환하는 함수
    fun convertAgeToRange(age: Int): String {
        return when (age) {
            in 0..9 -> "10대 미만"
            in 10..19 -> "10대"
            in 20..29 -> "20대"
            in 30..39 -> "30대"
            in 40..49 -> "40대"
            in 50..59 -> "50대"
            in 60..69 -> "60대"
            in 70..79 -> "70대"
            in 80..89 -> "80대"
            else -> "90대 이상"
        }
    }

    //연령대를 나이로 변환하는 함수
    fun convertRangeToAge(ageRange: String): Pair<Int, Int>? {
        return when (ageRange) {
            "10대 미만" -> Pair(0, 9)
            "10대" -> Pair(10, 19)
            "20대" -> Pair(20, 29)
            "30대" -> Pair(30, 39)
            "40대" -> Pair(40, 49)
            "50대" -> Pair(50, 59)
            "60대" -> Pair(60, 69)
            "70대" -> Pair(70, 79)
            "80대" -> Pair(80, 89)
            "90대 이상" -> Pair(90, 999)
            else -> null
        }
    }
}