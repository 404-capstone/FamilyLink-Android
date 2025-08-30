package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.album.model.Photo
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder

@Composable
fun PhotoDetailBottomSheet(
    photo: Photo,
    groupMembers: List<Pair<Int, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 제목
        Text(
            text = photo.title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextBlack
        )
        // 장소
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = "장소",
                tint = TextBlack,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = photo.area.ifEmpty { " " },
                style = MaterialTheme.typography.bodySmall,
                color = TextBlack,
                minLines = 1,
                maxLines = 1
            )
        }
        // 설명
        Text(
            text = photo.content.ifEmpty { " " },
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack,
            minLines = 3
        )
        // 참여자
        Text(
            text = "참여자",
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )

        if (photo.userIds.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                photo.userIds.forEach { participantId ->
                    val member = groupMembers.find { it.first == participantId }
                    member?.let { (userId, role) ->
                        SimpleRoleChip(
                            label = role,
                            userId = userId,
                            groupMembers = groupMembers
                        )
                    }
                }
            }
        } else {
            Text(
                text = " ",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}

// 역할 칩
@Composable
private fun SimpleRoleChip(
    label: String,
    userId: Int,
    groupMembers: List<Pair<Int, String>>
) {
    val (role, order) = remember(label) { parseRoleAndOrder(label) }
    val color = remember(role, order) { role.getColor(order) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 역할 색상 원
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        // 역할 명
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )
    }
}