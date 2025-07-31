package com.example.capstone_404.feature.group.model

import androidx.compose.ui.graphics.Color
import com.example.capstone_404.ui.theme.*

// 역할 별 색상 반환
fun RoleType.getColor(order: OrderType? = null): Color {
    return when (this) {
        RoleType.GRANDPA -> Grandpa
        RoleType.GRANDMA -> grandma
        RoleType.DAD -> Dad
        RoleType.MOM -> Mom
        RoleType.SON -> order?.getSonColor() ?: FirstSon
        RoleType.DAUGHTER -> order?.getDaughterColor() ?: FirstDaughter
    }
}

// Order 별 아들 색상 반환
fun OrderType.getSonColor(): Color {
    return when (this) {
        OrderType.FIRST -> FirstSon
        OrderType.SECOND -> SecondSon
        OrderType.THIRD -> ThirdSon
        OrderType.FOURTH -> FourthSon
        OrderType.FIFTH -> FifthSon
    }
}

// Order 별 딸 색상 반환
fun OrderType.getDaughterColor(): Color {
    return when (this) {
        OrderType.FIRST -> FirstDaughter
        OrderType.SECOND -> SecondDaughter
        OrderType.THIRD -> ThirdDaughter
        OrderType.FOURTH -> FourthDaughter
        OrderType.FIFTH -> FifthDaughter
    }
}

// 그룹원 리스트에 색상 표시를 위해 <역할, 순번> 분류
fun parseRoleAndOrder(roleStr: String): Pair<RoleType, OrderType?> {
    return when {
        roleStr.contains("아들") -> {
            val order = when {
                roleStr.contains("첫째") -> OrderType.FIRST
                roleStr.contains("둘째") -> OrderType.SECOND
                roleStr.contains("셋째") -> OrderType.THIRD
                roleStr.contains("넷째") -> OrderType.FOURTH
                roleStr.contains("다섯째") -> OrderType.FIFTH
                else -> null
            }
            RoleType.SON to order
        }
        roleStr.contains("딸") -> {
            val order = when {
                roleStr.contains("첫째") -> OrderType.FIRST
                roleStr.contains("둘째") -> OrderType.SECOND
                roleStr.contains("셋째") -> OrderType.THIRD
                roleStr.contains("넷째") -> OrderType.FOURTH
                roleStr.contains("다섯째") -> OrderType.FIFTH
                else -> null
            }
            RoleType.DAUGHTER to order
        }
        roleStr.contains("아빠") -> RoleType.DAD to null
        roleStr.contains("엄마") -> RoleType.MOM to null
        roleStr.contains("할아버지") -> RoleType.GRANDPA to null
        roleStr.contains("할머니") -> RoleType.GRANDMA to null
        else -> RoleType.SON to null // 기본값
    }
}