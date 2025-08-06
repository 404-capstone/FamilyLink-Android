package com.example.capstone_404.feature.mypage.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.capstone_404.R
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun UserInfoCard(
    nickname: String,
    socialProvider: String,
    profileImageUrl: String? = null,
    onProfileEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(212.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, Stroke),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지
            AsyncImage(
                model = profileImageUrl ?: R.drawable.default_profile,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Stroke)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 이름
            Text(
                text = "$nickname 님",
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 소셜 로그인 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 소셜 로그인 아이콘
                AsyncImage(
                    model = when (socialProvider) {
                        "NAVER" -> R.drawable.ic_naver
                        "KAKAO" -> R.drawable.ic_kakao
                        else -> R.drawable.ic_help // 기본값
                    },
                    contentDescription = "$socialProvider 아이콘",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (socialProvider) {
                        "NAVER" -> "네이버 로그인"
                        "KAKAO" -> "카카오 로그인"
                        else -> "$socialProvider 로그인"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBlack
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 프로필 편집 버튼
            ButtonDefault(
                text = "프로필 편집",
                onClick = onProfileEdit
            )
        }
    }
}