package com.example.capstone_404.feature.group.model

import com.example.capstone_404.data.info.SurveyResult

// 원점수에 따른 퍼센트 매핑
val scoreToPercentageMap = mapOf(
    50 to 99, 49 to 97, 48 to 96, 47 to 94, 46 to 90,
    45 to 88, 44 to 86, 43 to 83, 42 to 80, 41 to 74,
    40 to 70, 39 to 65, 38 to 61, 37 to 58, 36 to 50,
    35 to 44, 34 to 40, 33 to 36, 32 to 32, 31 to 28,
    30 to 24, 29 to 20, 28 to 18, 27 to 15, 26 to 14,
    25 to 13, 24 to 12
)

// 결과 계산 함수
fun calculateSurveyResult(totalScore: Int): SurveyResult {
    val percent = scoreToPercentageMap[totalScore] ?: 10

    return when (totalScore) {
        in 44..50 -> SurveyResult("매우 높음\n86~99%", totalScore, percent)
        in 40..43 -> SurveyResult("높음\n70~83%", totalScore, percent)
        in 36..39 -> SurveyResult("중간\n50~65%", totalScore, percent)
        in 30..35 -> SurveyResult("낮음\n24~44%", totalScore, percent)
        else -> SurveyResult("매우 낮음\n10~20%", totalScore, percent)
    }
}

// 레벨에 따른 설명 반환
fun getSurveyDescription(level: String): String = when (level) {
    "매우 높음\n86~99%" -> " 본인 가족의 의사소통의 질과 양에 대하여 매우 긍정적으로 느낍니다."
    "높음\n70~83%" -> " 본인 가족의 의사소통에 대하여 좋게 느끼며, 이에 대한 걱정이 거의 없습니다."
    "중간\n50~65%" -> " 본인 가족의 의사소통에 대하여 어느 정도 좋게 느끼지만, 약간의 걱정이 있습니다."
    "낮음\n24~44%" -> " 본인 가족의 의사소통의 질에 대하여 꽤 걱정을 하고 있습니다."
    "매우 낮음\n10~20%" -> " 본인 가족의 의사소통의 질에 대하여 매우 걱정을 하고 있습니다."
    else -> "설문 결과가 없습니다.\n먼저 설문 작성을 진행해주세요!"
}