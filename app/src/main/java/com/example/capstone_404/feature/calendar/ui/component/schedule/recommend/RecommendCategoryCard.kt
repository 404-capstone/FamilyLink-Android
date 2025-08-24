package com.example.capstone_404.feature.calendar.ui.component.schedule.recommend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.data.retrofit.model.response.RecommendCategory
import com.example.capstone_404.data.retrofit.model.response.RecommendItem
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun RecommendCategoryCard(
    category: RecommendCategory,
    selected: List<RecommendItem>,
    onToggle: (RecommendItem) -> Unit,
    onShowDetail: (RecommendItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = category.category,
            style = MaterialTheme.typography.headlineSmall,
            color = TextBlack
        )

        category.items.take(5).forEach { item ->
            RecommendItemRow(
                item = item,
                checked = selected.contains(item),
                onInfo = { onShowDetail(item) },
                onToggle = { onToggle(item) }
            )
        }
    }
}

@Composable
private fun RecommendItemRow(
    item: RecommendItem,
    checked: Boolean,
    onInfo: () -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이템 카드 Row
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Stroke, RoundedCornerShape(8.dp))
                .clickable { onInfo() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 활동 정보 Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 활동명
                Text(
                    text = item.activity,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                // 활동 설명
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            // 정보 아이콘
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "상세",
                tint = TextBlack,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        // 선택 토글
        Icon(
            painter = painterResource(if (checked) R.drawable.ic_check_circle else R.drawable.ic_close),
            contentDescription = null,
            tint = if (checked) Main else Stroke,
            modifier = Modifier
                .size(32.dp)
                .clickable { onToggle() }
        )
    }
}