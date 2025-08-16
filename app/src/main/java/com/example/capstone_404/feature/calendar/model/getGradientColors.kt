package com.example.capstone_404.feature.calendar.model

import androidx.compose.ui.graphics.Color
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Sub
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

// 캘린더 메인용
fun getGradientColors(
    schedule: Schedule,
    userIdToRole: Map<Int, String>
): List<Color> {
    val participants = schedule.participantUserIds.orEmpty()

    val baseColors: List<Color> =
        if (participants.isNotEmpty()) {
            // 그룹 일정일 경우
            participants.mapNotNull { userId ->
                userIdToRole[userId]?.let { role ->
                    val splitRole = parseRoleAndOrder(role)
                    splitRole.first.getColor(splitRole.second)
                }
            }
        } else {
            // 개인 일정일 경우
            userIdToRole.values.map { role ->
                val splitRole = parseRoleAndOrder(role)
                splitRole.first.getColor(splitRole.second)
            }
        }

    val distinct = baseColors.distinct()
    return when {
        distinct.size >= 2 -> distinct
        distinct.size == 1 -> listOf(distinct.first(), distinct.first())
        else -> listOf(Main, Sub, Error)
    }
}

// 개인 일정 추가용
fun colorForUserId(userId: Int, userIdToRole: Map<Int, String>): Color {
    val roleLabel = userIdToRole[userId] ?: return Error
    val splitRole = parseRoleAndOrder(roleLabel)
    return splitRole.first.getColor(splitRole.second)
}

// 최적화 결과 비교용
fun gradientForPersonal(userId: Int?, userIdToRole: Map<Int, String>): List<Color> {
    val color = userId?.let { colorForUserId(it, userIdToRole) } ?: Error
    return listOf(color, color)
}
fun gradientForGroup(memberIds: Collection<Int>, userIdToRole: Map<Int, String>): List<Color> {
    val colors = memberIds.mapNotNull { id ->
        runCatching { colorForUserId(id, userIdToRole) }.getOrNull()
    }.distinct()
    return when {
        colors.size >= 2 -> colors
        colors.size == 1 -> listOf(colors.first(), colors.first())
        else -> listOf(Main, Sub, Error)
    }
}