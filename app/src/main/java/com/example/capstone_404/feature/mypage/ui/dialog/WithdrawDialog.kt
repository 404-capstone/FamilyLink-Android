package com.example.capstone_404.feature.mypage.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.capstone_404.ui.component.ButtonColorRed
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack

// 회원 탈퇴 확인 다이얼로그
@Composable
fun WithdrawDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 메인 질문
                Text(
                    text = "정말 탈퇴하시겠어요?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 안내 텍스트
                Text(
                    text = "탈퇴 버튼 선택 시, 계정은 삭제되며 복구되지 않습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 취소 버튼
                    ButtonOutline(
                        text = "취소",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    // 탈퇴 버튼
                    ButtonColorRed(
                        text = "탈퇴",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}