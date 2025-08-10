package com.example.capstone_404.feature.group.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.capstone_404.data.retrofit.model.response.GroupUserInfoData
import com.example.capstone_404.feature.group.model.getDefaultImage
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.utils.getColor
import com.example.capstone_404.utils.parseRoleAndOrder
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun GroupUserItem(
    user: GroupUserInfoData,
    isCurrentUser: Boolean,
    onClick: () -> Unit
) {
    // 역할 텍스트 나누기
    val splitRole = parseRoleAndOrder(user.role)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, Stroke, RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 프로필 이미지
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Stroke),
                contentAlignment = Alignment.Center
            ) {
                if (user.image == null) {
                    Image(
                        painter = painterResource(id = getDefaultImage(user.role)),
                        contentDescription = "기본 그룹 이미지",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AsyncImage(
                        model = user.image,
                        contentDescription = "프로필 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

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
                                .background(Stroke)
                                .border(1.dp, Main, RoundedCornerShape(100)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "나",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextBlack,
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
                        text = "${user.age}대",
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
}