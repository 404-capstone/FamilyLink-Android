package com.example.capstone_404.ui.component.dialog

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.capstone_404.ui.component.ButtonColorRed
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack

// 취소, 삭제 등에 사용할 다이얼로그
@Composable
fun ActionDialog(
    title: String,
    description: String,
    confirmText: String,
    cancelText: String = "취소",
    showDropdown: Boolean = false,
    dropdownContent: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmEnabled: Boolean = true
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 타이틀
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(8.dp))
                // 본문 내용
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )

                // 드롭다운(그룹장 탈퇴용)
                if (showDropdown && dropdownContent != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    dropdownContent()
                }

                Spacer(modifier = Modifier.height(24.dp))
                // 버튼 Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ButtonOutline(
                        text = cancelText,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    ButtonColorRed(
                        text = confirmText,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        enabled = confirmEnabled
                    )
                }
            }
        }
    }
}