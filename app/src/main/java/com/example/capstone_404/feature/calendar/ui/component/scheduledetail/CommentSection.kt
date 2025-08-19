package com.example.capstone_404.feature.calendar.ui.component.scheduledetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.calendar.model.gradientForPersonal
import com.example.capstone_404.feature.calendar.model.scheduledetail.CommentUi
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// 댓글 경계 문구
@Composable
fun SectionDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Stroke)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Stroke)
        )
    }
}

// 댓글 영역
@Composable
fun CommentSection(
    comments: List<CommentUi>,
    userIdToRole: Map<Int, String>,
    currentUserId: Int?
) {
    if (comments.isEmpty()) {
        Text(
            text = "등록된 댓글이 없습니다",
            style = MaterialTheme.typography.bodyMedium,
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        // 날짜별 그룹화
        val dateByComment = remember(comments) {
            comments.groupBy { comment -> LocalDate.parse(comment.dateAt) }.toSortedMap()
        }
        val dateFormatter = remember {
            DateTimeFormatter.ofPattern("yyyy년 M월 d일 EEEE", Locale.KOREAN)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            dateByComment.forEach { (date, items) ->
                // 댓글 등록 날짜
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DateHeader(text = date.format(dateFormatter))
                }
                // 해당 날짜의 댓글
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items.forEach { comment ->
                        val isMine = currentUserId != null && comment.userId == currentUserId
                        val roleLabel =
                            if (!isMine) comment.userId?.let { userIdToRole[it] } else null
                        val roleColors = if (!isMine) comment.userId?.let {
                            gradientForPersonal(it, userIdToRole)
                            } else null
                        // 댓글
                        CommentRow(
                            text = comment.body,
                            isMine = isMine,
                            writerRole = roleLabel,
                            roleColors = roleColors
                        )
                    }
                }
            }
        }
    }
}

// 댓글 등록 날짜 해더
@Composable
private fun DateHeader(text: String) {
    Surface(
        shape = RoundedCornerShape(100),
        color = ButtonDisabled,
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextBlack,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// 댓글 Row
@Composable
private fun CommentRow(
    text: String,
    isMine: Boolean,
    writerRole: String?,
    roleColors: List<Color>? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            // 상대방 댓글은 역할 출력
            if (!isMine && writerRole != null && roleColors != null) {
                CommentWriter(writerRole, roleColors)
            }
            Surface(
                shape = if (!isMine) RoundedCornerShape(topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                        else RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                color = if (isMine) ButtonDisabled else Color.White,
                border = BorderStroke(1.dp, Stroke),
                tonalElevation = 0.dp
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// 댓글 작성자 표시
@Composable
private fun CommentWriter(
    label: String,
    colors: List<Color>
) {
    val brush = remember(colors) { Brush.sweepGradient(colors) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(brush)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextBlack
        )
    }
}