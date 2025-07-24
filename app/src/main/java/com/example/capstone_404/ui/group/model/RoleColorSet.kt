package com.example.capstone_404.ui.group.model

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