package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.diary.model.Responder
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun QuestionExpandedCard(
    index: Int,
    authorChips: List<Responder>,
    text: String,
    answers: List<Pair<String, String>>,
    onClickCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, Stroke, RoundedCornerShape(8.dp))
            .clickable(onClick = onClickCollapse),
        colors = cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "질문 ${index + 1}",
                    style = typography.headlineSmall,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "작성자", style = typography.bodyLarge, color = TextBlack)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (authorChips.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        authorChips.forEach { chip ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .height(12.dp)
                                        .width(12.dp)
                                        .border(0.dp, chip.color, CircleShape)
                                        .background(chip.color)
                                )
                                Text(
                                    text = chip.roleLabel,
                                    style = typography.labelSmall,
                                    color = TextBlack
                                )
                            }
                        }
                    }
                } else {
                    // 작성자가 없을 때 표시
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .height(12.dp)
                                .width(12.dp)
                                .background(Color.Gray)
                        )
                        Text(
                            text = "작성자 없음",
                            style = typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "질문 내용", style = typography.bodyLarge, color = TextBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, style = typography.bodyMedium, color = TextBlack)

            Spacer(modifier = Modifier.height(16.dp))
            answers.forEachIndexed { idx, pair ->
                // 역할에 해당하는 색상 찾기
                val roleColor = authorChips.find { it.roleLabel == pair.first }?.color ?: Color.Gray
                AnswerItem(role = pair.first, text = pair.second, roleColor = roleColor)
                if (idx != answers.lastIndex) Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
