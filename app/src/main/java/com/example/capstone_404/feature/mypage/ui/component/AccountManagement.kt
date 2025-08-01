package com.example.capstone_404.feature.mypage.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun AccountManagement(
    onWithdraw: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(0.5.dp, Stroke),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 계정 관리 타이틀
            Text(
                text = "계정 관리",
                style = MaterialTheme.typography.titleMedium,
                color = TextBlack,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 회원 탈퇴 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWithdraw() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_withdraw),
                    contentDescription = "회원 탈퇴",
                    tint = Error,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "회원 탈퇴",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Error
                )
            }

            // 구분선
            Spacer(modifier = Modifier.height(12.dp))
            androidx.compose.material3.Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Stroke
            )
            Spacer(modifier = Modifier.height(10.dp))

            // 로그아웃 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLogout() }
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = "로그아웃",
                    tint = TextBlack,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "로그아웃",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
            }
        }
    }
}