package com.example.capstone_404.feature.calendar.ui.component.scheduleadd

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.dateFormatter
import com.example.capstone_404.feature.calendar.model.gradientForGroup
import com.example.capstone_404.feature.calendar.model.gradientForPersonal
import com.example.capstone_404.feature.calendar.model.serverDateTimeFormatter
import com.example.capstone_404.feature.calendar.model.toSchedule
import com.example.capstone_404.feature.calendar.ui.component.ScheduleItem
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizeResultSheet(
    data: OptimizeData,
    userIdToRole: Map<Int, String>,
    selectedMemberIds: Set<Int>,
    groupTitle: String,
    onCancel: () -> Unit,
    onApply: (LocalDate, OptimizeData) -> Unit
) {
    // 참여자 안정적 정의
    val participantDefined = remember(data, userIdToRole) {
        userIdToRole.toMutableMap().apply {
            (data.beforeSchedule.personalSchedule + data.afterSchedule.personalSchedule).forEach {
                if (!containsKey(it.memberId)) put(it.memberId, it.memberPosition)
            }
        }
    }

    // 선택된 날짜 정의
    val pickedDate: LocalDate? = remember(data) {
        val date = data.afterSchedule.groupSchedule?.startTime
            ?: data.beforeSchedule.personalSchedule.firstOrNull()?.startTime
        date?.let { LocalDateTime.parse(it, serverDateTimeFormatter).toLocalDate() }
    }

    val canApply = pickedDate != null

    // 최적화 결과 Schedule 타입으로 변환
    val beforeSchedules = remember(data) {
        data.beforeSchedule.personalSchedule.map { it.toSchedule() }
    }
    val afterPersonalSchedules = remember(data) {
        data.afterSchedule.personalSchedule.map { it.toSchedule() }
    }
    val afterGroupSchedule = remember(data, selectedMemberIds, groupTitle) {
        data.afterSchedule.groupSchedule?.toSchedule(selectedMemberIds, groupTitle)
    }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Stroke) },
        containerColor = Color.White
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            val buttonHeight = 72.dp
            val bodyMaxHeight = (maxHeight - buttonHeight).coerceAtLeast(120.dp)

            Column(
                modifier = Modifier
                    .fillMaxWidth().
                    padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = bodyMaxHeight)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    pickedDate?.let { date ->
                        Text(
                            text = date.format(dateFormatter),
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextBlack
                        )
                    }
                    // 기존 일정
                    OptimizeResultCard(
                        title = "기존 일정",
                        schedules = beforeSchedules,
                        userIdToRole = participantDefined
                    )
                    // 최적화된 일정
                    OptimizeResultCard(
                        title = "최적화된 일정",
                        schedules = afterPersonalSchedules,
                        groupSchedule = afterGroupSchedule,
                        userIdToRole = participantDefined
                    )
                    Spacer(Modifier.height(0.dp))
                }

                // 하단 고정 버튼
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ButtonOutline(
                        text = "취소",
                        modifier = Modifier.weight(1f),
                        onClick = onCancel
                    )
                    ButtonDefault(
                        text = "최적화 적용",
                        modifier = Modifier.weight(1f),
                        enabled = canApply,
                        onClick = { pickedDate?.let { onApply(it, data) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptimizeResultCard(
    title: String,
    schedules: List<Schedule>,
    groupSchedule: Schedule? = null,
    userIdToRole: Map<Int, String>
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Background,
        border = BorderStroke(1.dp, Stroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlack
            )

            schedules.forEach { schedule ->
                val gradientColors = if (schedule.isGroup) {
                    gradientForGroup(schedule.participantUserIds.orEmpty(), userIdToRole)
                } else {
                    gradientForPersonal(schedule.writerId, userIdToRole)
                }
                ScheduleItem(
                    schedule = schedule,
                    userIdToRole = userIdToRole,
                    gradientColors = gradientColors,
                    onlyTime = true
                )
            }

            groupSchedule?.let { schedule ->
                val gradientColors = gradientForGroup(schedule.participantUserIds.orEmpty(), userIdToRole)
                ScheduleItem(
                    schedule = schedule,
                    userIdToRole = userIdToRole,
                    gradientColors = gradientColors,
                    onlyTime = true
                )
            }
        }
    }
}