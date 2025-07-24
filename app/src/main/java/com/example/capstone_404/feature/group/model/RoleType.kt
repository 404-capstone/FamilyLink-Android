package com.example.capstone_404.feature.group.model

// 가족 그룹 내 역할 타입 정의
enum class RoleType {
    GRANDPA,
    GRANDMA,
    DAD,
    MOM,
    SON,
    DAUGHTER
}

// 역할 한글 번역
fun RoleType.toKorean(): String = when (this) {
    RoleType.GRANDPA -> "할아버지"
    RoleType.GRANDMA -> "할머니"
    RoleType.DAD -> "아빠"
    RoleType.MOM -> "엄마"
    RoleType.SON -> "아들"
    RoleType.DAUGHTER -> "딸"
}