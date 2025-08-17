package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.EmotionResult
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun ResultSectionCard(
    title: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Stroke),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(16.dp))
            content()
        }
    }
}

@Composable
fun DiaryContentSection(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextBlack,
        modifier = modifier
    )
}

@Composable
fun EmotionAnalysisSection(
    emotions: List<EmotionResult>,
    modifier: Modifier = Modifier
) {
    // 감정 상위 3개 추출
    val top3Emotion = emotions.sortedByDescending { it.percentage }.take(3)
    // 원그래프 정규화
    val total = top3Emotion.sumOf { it.percentage.toDouble() }.toFloat().let { if (it <= 0f) 1f else it }
    val top3ForChart = top3Emotion.map { it.copy(percentage = it.percentage / total) }


    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Stroke)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                EmotionPieChart(emotions = top3ForChart)
                // 색상 (상위 3개)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    top3Emotion.forEach { emotion ->
                        EmotionDotLabelItem(emotion)
                    }
                }
            }
        }

        // 감정 아이콘 + 비율
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            top3Emotion.forEach { emotion ->
                EmotionItem(emotion)
            }
        }
    }
}

@Composable
private fun EmotionItem(
    emotion: EmotionResult,
    modifier: Modifier = Modifier
) {
    val iconRes = when (emotion.emotion) {
        "기쁨" -> R.drawable.ic_smile
        "혐오" -> R.drawable.ic_disgust
        "슬픔" -> R.drawable.ic_sad
        "분노" -> R.drawable.ic_angry
        "상처" -> R.drawable.ic_hurt
        "놀람" -> R.drawable.ic_surprise
        else -> R.drawable.ic_smile
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = "${String.format("%.2f", emotion.percentage * 100)}%",
            style = MaterialTheme.typography.bodySmall,
            color = TextBlack
        )
    }
}

@Composable
private fun EmotionDotLabelItem(
    emotion: EmotionResult,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = emotion.color,
                    shape = CircleShape
                )
        )
        Text(text = emotion.emotion, style = MaterialTheme.typography.bodySmall, color = TextBlack)
    }
}

@Composable
fun AiFeedbackSection(
    feedback: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = feedback,
        style = MaterialTheme.typography.bodyMedium,
        color = TextBlack,
        modifier = modifier
    )
}
