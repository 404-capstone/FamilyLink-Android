package com.example.capstone_404.feature.group.model

// 질문 데이터 정의
data class SurveyQuestion(
    val id: Int,
    val question: String
)

val surveyQuestions = listOf(
    SurveyQuestion(1, "우리 가족은 서로 의사소통하는 방법에 만족한다."),
    SurveyQuestion(2, "우리 가족은 서로 이야기를 잘 들어준다."),
    SurveyQuestion(3, "우리 가족은 서로 애정을 표현한다."),
    SurveyQuestion(4, "우리 가족은 상대에게 원하는 것을 요청할 수 있다."),
    SurveyQuestion(5, "우리 가족은 문제가 있을 때 침착하게 함께 의논할 수 있다."),
    SurveyQuestion(6, "우리 가족은 생각이나 신념을 서로 의논할 수 있다."),
    SurveyQuestion(7, "우리 가족은 서로에 대한 질문을 할 때, 솔직한 대답을 들을 수 있다."),
    SurveyQuestion(8, "우리 가족은 서로의 감정을 이해하려고 노력한다."),
    SurveyQuestion(9, "화가 났을 때, 우리 가족은 서로에 대해서 부정적인 말을 하는 일이 거의 없다."),
    SurveyQuestion(10, "우리 가족은 서로 솔직하게 감정표현을 한다.")
)