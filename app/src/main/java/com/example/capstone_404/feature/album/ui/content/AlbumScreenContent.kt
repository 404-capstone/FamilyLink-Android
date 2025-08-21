package com.example.capstone_404.feature.album.ui.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.album.model.Photo
import com.example.capstone_404.feature.album.ui.component.AlbumItem
import com.example.capstone_404.feature.album.ui.component.PhotoGridItem
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

// 그룹 가입한 상태의 앨범 화면
@Composable
fun AlbumScreenContent(
    albums: Map<String, List<Photo>>,
    selectedYearMonth: String?,
    onYearMonthClick: (String?) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            // 앨범이 비어있을 때
            albums.isEmpty() -> {
                EmptyAlbumState(
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 년월 선택하지 않았을 때 - 2열 그리드 앨범 목록
            selectedYearMonth == null -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = albums.entries.toList()
                            .sortedByDescending { it.key },  // 최신순 정렬
                        key = { it.key }
                    ) { (yearMonth, photos) ->
                        AlbumItem(
                            yearMonth = yearMonth,
                            photoCount = photos.size,
                            thumbnailUrl = photos
                                .maxByOrNull { it.time }  // 최신 사진을 썸네일로
                                ?.thumbnailUrl,
                            onClick = { onYearMonthClick(yearMonth) }
                        )
                    }
                }
            }

            // 년월 선택했을 때 - 3열 사진 그리드
            else -> {
                val photos = albums[selectedYearMonth] ?: emptyList()

                if (photos.isEmpty()) {
                    EmptyAlbumState(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = photos.sortedByDescending { it.time },  // 최신순
                            key = { it.id }
                        ) { photo ->
                            PhotoGridItem(
                                photo = photo,
                                onClick = {
                                    // TODO: 사진 상세 보기 화면으로 이동
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 빈 앨범 상태
@Composable
private fun EmptyAlbumState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, Stroke),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "함께한 추억을 공유해 보세요.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "우측 하단 버튼을 눌러\n사진을 추가할 수 있어요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Main,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
