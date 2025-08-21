package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.capstone_404.feature.album.model.toKoreanYearMonth
import com.example.capstone_404.ui.theme.*

// 년월별 앨범 아이템
@Composable
fun AlbumItem(
    yearMonth: String,
    photoCount: Int,
    thumbnailUrl: String?,  // 최신 사진 썸네일
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),  // 정사각형 비율
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Stroke),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 썸네일 이미지 (있을 경우)
            if (!thumbnailUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // 그라데이션 오버레이
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.3f)
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)  // 프로젝트 배경색
                )
            }
            
            // 텍스트 정보
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = yearMonth.toKoreanYearMonth(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (!thumbnailUrl.isNullOrEmpty()) TextWhite else TextBlack
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "사진 ${photoCount}장",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!thumbnailUrl.isNullOrEmpty()) TextWhite else TextGray
                )
            }
        }
    }
}
