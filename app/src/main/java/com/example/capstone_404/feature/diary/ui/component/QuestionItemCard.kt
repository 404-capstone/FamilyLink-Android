package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.Responder
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun QuestionItemCard(
    question: Question,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        border = BorderStroke(0.5.dp, Stroke),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // 날짜
                Text(
                    text = question.date,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 응답자 표시
                RespondersRow(responders = question.responders)
            }

            // 화살표 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_navigate_right),
                contentDescription = null,
                tint = TextBlack,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RespondersRow(
    responders: List<Responder>
) {
    val dotSize = 12.dp
    val rowHeight = 20.dp
    if (responders.isEmpty()) {
        Spacer(modifier = Modifier.height(rowHeight))
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        responders.forEach { responder ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .padding(0.dp)
                    .background(responder.color, CircleShape)
            )
        }
    }
}