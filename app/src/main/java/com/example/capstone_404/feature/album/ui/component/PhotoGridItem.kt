package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.capstone_404.feature.album.model.Photo
import com.example.capstone_404.ui.theme.Background

// 사진 그리드 아이템
@Composable
fun PhotoGridItem(
    photo: Photo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),  // 정사각형
        shape = RectangleShape,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        AsyncImage(
            model = photo.thumbnailUrl,
            contentDescription = photo.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(Background),
            error = ColorPainter(Background)
        )
    }
}
