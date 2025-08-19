package com.example.capstone_404.feature.calendar.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.data.retrofit.model.response.ScheduleDetailData
import com.example.capstone_404.feature.calendar.model.dateFormatter
import com.example.capstone_404.feature.calendar.model.schedule.detail.CommentUi
import com.example.capstone_404.feature.calendar.model.serverDateTimeFormatter
import com.example.capstone_404.feature.calendar.model.timeFormatter
import com.example.capstone_404.feature.calendar.ui.component.schedule.detail.CommentSection
import com.example.capstone_404.feature.calendar.ui.component.schedule.detail.ParticipantsSection
import com.example.capstone_404.feature.calendar.ui.component.schedule.detail.ReadonlyRow
import com.example.capstone_404.feature.calendar.ui.component.schedule.detail.SectionDivider
import com.example.capstone_404.feature.calendar.ui.component.schedule.detail.TitleWithBar
import java.time.LocalDateTime

@Composable
fun ScheduleDetailContent(
    data: ScheduleDetailData,
    writerId: Int?,
    title: String,
    userId: Int?,
    userIdToRole: Map<Int, String>,
    onToggleJoin: (join: Boolean) -> Unit
) {
    val start = remember(data.startTime) { LocalDateTime.parse(data.startTime, serverDateTimeFormatter) }
    val end = remember(data.endTime) { LocalDateTime.parse(data.endTime, serverDateTimeFormatter) }

    val startDate = remember(start) { dateFormatter.format(start.toLocalDate()) }
    val startTime = remember(start) { timeFormatter.format(start.toLocalTime()) }
    val endDate = remember(end) { dateFormatter.format(end.toLocalDate()) }
    val endTime = remember(end) { timeFormatter.format(end.toLocalTime()) }

    val isGroup = remember(data.participantIds) { data.participantIds.isNotEmpty() }

    // 참여자 추출
    val participantIds = remember(data.participantIds, writerId) {
        data.participantIds.ifEmpty { listOfNotNull(writerId) }
    }

    // 가족 일정에 현재 사용자 참여 여부
    val iAmJoined = remember(userId, data.participantIds) {
        userId != null && data.participantIds.contains(userId)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 제목
        TitleWithBar(
            title = title,
            isGroup = isGroup,
            writerId = writerId,
            participantIds = participantIds,
            userIdToRole = userIdToRole
        )
        // 시작 시간
        ReadonlyRow(
            icon = R.drawable.ic_start,
            left = startDate,
            right = startTime
        )
        // 종료 시간
        ReadonlyRow(
            icon = R.drawable.ic_end,
            left = endDate,
            right = endTime
        )
        // 장소
        if (!data.location.isNullOrBlank()) {
            ReadonlyRow(
                icon = R.drawable.ic_location,
                left = data.location
            )
        }
        // 메모
        if (!data.content.isNullOrBlank()) {
            ReadonlyRow(
                icon = R.drawable.ic_help,
                left = data.content
            )
        }
        // 참여자
        ParticipantsSection(
            isGroup = isGroup,
            participantIds = participantIds,
            writerId = writerId,
            userId = userId,
            iAmJoined = iAmJoined,
            userIdToRole = userIdToRole,
            onToggleJoin = onToggleJoin
        )
        // 댓글
        SectionDivider(label = "댓글")

        val commentItems = remember(data.comments) {
            data.comments.map { comment ->
                CommentUi(
                    body = comment.body,
                    dateAt = comment.dateAt,
                    userId = comment.userId
                )
            }
        }
        CommentSection(
            comments = commentItems,
            userIdToRole = userIdToRole,
            currentUserId = userId
        )
    }
}