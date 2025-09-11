package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.Responder
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun AccordionQuestionCard(
    index: Int,
    authorChips: List<Responder>,
    text: String,
    answers: List<Pair<String, String>>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(0.5.dp, Stroke, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 헤더
            QuestionHeader(
                index = index,
                text = text,
                isExpanded = isExpanded
            )
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ExpandedContent(
                    authorChips = authorChips,
                    text = text,
                    answers = answers
                )
            }
        }
    }
}

@Composable
private fun QuestionHeader(
    index: Int,
    text: String,
    isExpanded: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "질문 ${index + 1}",
                style = MaterialTheme.typography.headlineSmall,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!isExpanded) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        SimpleArrowIcon(isExpanded = isExpanded)
    }
}

@Composable
private fun SimpleArrowIcon(isExpanded: Boolean) {
    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "icon_rotation"
    )

    Icon(
        painter = painterResource(id = R.drawable.ic_arrow_down),
        contentDescription = if (isExpanded) "접기" else "펼치기",
        tint = TextBlack,
        modifier = Modifier
            .padding(start = 8.dp)
            .rotate(iconRotation)
    )
}

@Composable
private fun ExpandedContent(
    authorChips: List<Responder>,
    text: String,
    answers: List<Pair<String, String>>
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        // 작성자 영역
        Text(text = "작성자", style = MaterialTheme.typography.bodyLarge, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        if (authorChips.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                authorChips.forEach { chip ->
                    AuthorChip(chip = chip)
                }
            }
        } else {
            EmptyAuthorChip()
        }

        Spacer(modifier = Modifier.height(16.dp))
        // 전체 질문 내용
        Text(text = "질문 내용", style = MaterialTheme.typography.bodyLarge, color = TextBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextBlack)

        Spacer(modifier = Modifier.height(16.dp))
        // 답변들
        answers.forEachIndexed { idx, pair ->
            val roleColor = authorChips.find { it.roleLabel == pair.first }?.color ?: Color.Gray
            AnswerItem(role = pair.first, text = pair.second, roleColor = roleColor)
            if (idx != answers.lastIndex) Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AuthorChip(chip: Responder) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .height(12.dp)
                .width(12.dp)
                .border(0.dp, chip.color, CircleShape)
                .background(chip.color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = chip.roleLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )
    }
}

@Composable
private fun EmptyAuthorChip() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .height(12.dp)
                .width(12.dp)
                .background(Color.Gray)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "작성자 없음",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}