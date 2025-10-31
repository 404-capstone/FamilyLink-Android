package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun AnswerItem(
    role: String, 
    text: String, 
    roleColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, Stroke),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .height(12.dp)
                        .width(12.dp)
                        .border(0.dp, roleColor, CircleShape)
                        .background(roleColor)
                )
                Text(
                    text = role, 
                    style = typography.bodyMedium,
                    color = TextBlack
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = text, 
                style = typography.bodyMedium,
                color = TextBlack
            )
        }
    }
}
