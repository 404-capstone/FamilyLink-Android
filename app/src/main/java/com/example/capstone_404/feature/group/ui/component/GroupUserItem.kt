package com.example.capstone_404.feature.group.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.capstone_404.data.info.GroupUserInfo
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun GroupUserItem(
    user: GroupUserInfo,
    isCurrentUser: Boolean,
    onClick: () -> Unit
) {
    // 역할 텍스트 나누기
    val splitRole = parseRoleAndOrder(user.role)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(12.dp)
            .clickable { onClick() }
    ) {
        // 프로필 이미지
        AsyncImage(
            model = user.image,
            contentDescription = "프로필 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Stroke)
        )

        Spacer(modifier = Modifier.width(12.dp))
        // 정보 Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 이름 + 연령 Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Stroke),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "나",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextWhite,
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user.age,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextBlack
                )
            }
            // 색상 + 역할 Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(splitRole.first.getColor(splitRole.second))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBlack
                )
            }
        }
        // 그룹장 텍스트
        if (user.leader) {
            Text(
                text = "그룹장",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
    }
}