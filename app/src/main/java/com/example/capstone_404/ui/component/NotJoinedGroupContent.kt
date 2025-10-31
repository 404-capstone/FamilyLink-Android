package com.example.capstone_404.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun NotJoinedGroupContent(
    onNavigateToGroup: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "가입된 그룹이 없습니다",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "가입된 그룹이 없으면 기능 이용에 제한됩니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Error
                )
                Spacer(Modifier.height(12.dp))
                ButtonDefault(
                    text = "그룹 페이지로 이동",
                    onClick = onNavigateToGroup
                )
            }
        }
    }
}
