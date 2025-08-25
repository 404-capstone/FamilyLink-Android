package com.example.capstone_404.feature.calendar.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.capstone_404.data.retrofit.model.response.RecommendItem
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun RecommendDetailDialog(
    item: RecommendItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Background)
                .padding(16.dp)
        ) {
            // 활동 명
            LabeledLine(
                label = "활동 명",
                value = item.activity,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))
            // 장소
            LabeledLine(
                label = "장소",
                value = item.location
            )
            Spacer(Modifier.height(12.dp))
            // 설명
            LabeledLine(
                label = "설명",
                value = item.description
            )
            Spacer(Modifier.height(16.dp))
            // 닫기 버튼
            ButtonDefault(
                text = "닫기",
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun LabeledLine(
    label: String,
    value: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Column {
        Text(
            text = "[$label]",
            style = style,
            color = TextBlack,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = style,
            color = TextBlack,
            fontWeight = FontWeight.Medium
        )
    }
}