package com.example.capstone_404.feature.group.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun GroupInfoCard(
    groupName: String,
    groupImageUrl: String,
    isSingleUser: Boolean,
    onEditGroup: () -> Unit,
    onInvite: () -> Unit,
    onLeave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, Stroke),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp,start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 탈퇴 or 삭제 텍스트 버튼
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isSingleUser) "그룹 삭제" else "그룹 탈퇴",
                    color = Error,
                    modifier = Modifier.align(Alignment.TopEnd)
                        .clickable { onLeave() },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // 대표 사진
            AsyncImage(
                model = groupImageUrl,
                contentDescription = "그룹 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Stroke)
            )

            Spacer(modifier = Modifier.height(8.dp))
            // 그룹 이름
            Text(
                text = groupName,
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))
            // 그룹 정보 수정 버튼
            ButtonDefault(
                text = "그룹 정보 수정",
                onClick = onEditGroup
            )

            Spacer(modifier = Modifier.height(8.dp))
            // 그룹원 초대 버튼
            ButtonOutline(
                text = "그룹원 초대",
                onClick = onInvite
            )
        }
    }
}