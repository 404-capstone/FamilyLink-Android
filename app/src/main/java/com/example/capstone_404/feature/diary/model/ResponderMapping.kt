package com.example.capstone_404.feature.diary.model

import com.example.capstone_404.feature.group.model.OrderType
import com.example.capstone_404.feature.group.model.RoleType
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

// 역할 리스트를 그룹 색상 점과 함께 매핑
fun roleLabelsToResponders(roleLabels: List<String>): List<Responder> {
    return roleLabels.map { roleLabel ->
        val (roleType: RoleType, order: OrderType?) = parseRoleAndOrder(roleLabel)
        val color = roleType.getColor(order)
        Responder(roleLabel = roleLabel, color = color)
    }
}
