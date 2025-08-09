package com.example.capstone_404.feature.calendar.model

import androidx.compose.ui.graphics.Color
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Sub
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

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