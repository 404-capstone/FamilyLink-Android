package com.example.capstone_404.feature.mypage.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

@Composable
fun ProfileImage(
    imageUrl: String? = null,
    defaultImage: Int = R.drawable.default_profile,
    imageSize: Dp = 150.dp,
    changeText: String = "프로필 사진 변경",
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Stroke),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지
            AsyncImage(
                model = imageUrl ?: defaultImage,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape)
                    .background(Stroke)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 프로필 사진 변경 텍스트 (클릭 가능)
            Text(
                text = changeText,
                style = MaterialTheme.typography.bodyMedium,
                color = Main,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onImageClick() }
            )
        }
    }
}