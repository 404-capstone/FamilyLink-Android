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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.EmotionResult
import com.example.capstone_404.feature.diary.model.FamilyEmotion
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
        "행복" -> R.drawable.ic_happiness
        "혐오" -> R.drawable.ic_disgust
        "슬픔" -> R.drawable.ic_sad
        "분노" -> R.drawable.ic_angry
        "불안" -> R.drawable.ic_anxiety
        "놀람" -> R.drawable.ic_surprise
        else -> R.drawable.ic_happiness
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
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

// 가족들의 대표 감정
@Composable
fun FamilyEmotionsSection(
    familyEmotions: List<FamilyEmotion>?,
    modifier: Modifier = Modifier
) {
    when {
        familyEmotions == null -> {
            // 다이어리 조회 실패 시
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "그룹원의 감정을 조회하는 중 오류가 발생했습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
            }
        }
        familyEmotions.isEmpty() -> {
            // 빈 상태
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "아직 다른 그룹원이 일기를 작성하지 않았어요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
            }
        }
        else -> {
            // 감정 리스트 (2개 그리드)
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                familyEmotions.chunked(2).forEach { rowEmotions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowEmotions.forEach { emotion ->
                            FamilyEmotionItem(
                                familyEmotion = emotion,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 홀수 개일 때 빈 공간 채우기
                        if (rowEmotions.size == 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyEmotionItem(
    familyEmotion: FamilyEmotion,
    modifier: Modifier = Modifier
) {
    val iconRes = when (familyEmotion.emotion) {
        "행복" -> R.drawable.ic_happiness
        "혐오" -> R.drawable.ic_disgust
        "슬픔" -> R.drawable.ic_sad
        "분노" -> R.drawable.ic_angry
        "불안" -> R.drawable.ic_anxiety
        "놀람" -> R.drawable.ic_surprise
        else -> R.drawable.ic_happiness
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 역할 색상 원
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(familyEmotion.color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = familyEmotion.roleLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack,
            maxLines = 1
        )
        Spacer(Modifier.width(16.dp))

        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))

        Text(
            text = familyEmotion.emotion,
            style = MaterialTheme.typography.bodySmall,
            color = TextBlack
        )
    }
}