package com.example.capstone_404.feature.calendar.ui.component.schedule.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun ReadonlyRow(
    icon: Int,
    left: String,
    right: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = TextBlack
        )

        Spacer(Modifier.width(16.dp))
        // 왼쪽 텍스트
        Text(
            text = left,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        // 오른쪽 텍스트
        if (right != null) {
            Spacer(Modifier.width(8.dp))
            Text(
                text = right,
                style = MaterialTheme.typography.bodyMedium,
                color = TextBlack,
                maxLines = 1
            )
        }
    }
}