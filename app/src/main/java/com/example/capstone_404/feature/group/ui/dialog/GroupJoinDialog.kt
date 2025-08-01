package com.example.capstone_404.feature.group.ui.dialog

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.GuideWarningCard
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Stroke

@Composable
fun GroupJoinDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String) -> Unit,
    isError: Boolean
) {
    val codeLength = 6
    var inviteCode by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "초대 코드 입력",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "초대 코드 6자리를 입력해 주세요",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))
                // OTP 느낌의 입력 박스(출력용)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(codeLength) { index ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Stroke, RoundedCornerShape(8.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = inviteCode.getOrNull(index)?.toString() ?: "",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }

                // 실제 입력 필드(입력용)
                TextField(
                    value = inviteCode,
                    onValueChange = {
                        if (it.length <= codeLength && it.all { char -> char.isLetterOrDigit() || char == '_' })
                            inviteCode = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.dp)
                        .padding(0.dp),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 안내 문구
                GuideWarningCard("초대 코드는 그룹에 속한 사용자가\n“그룹원 초대”를 통해 발급 받을 수 있어요!")

                // 에러 메시지
                if (isError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "유효하지 않은 코드입니다.\n제공자에게 재발급을 요청해 보세요!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // 그룹 정보 조회 버튼
                ButtonDefault(
                    text = "그룹 조회",
                    onClick = {
                        onConfirm(inviteCode)
                        inviteCode = ""
                        },
                    enabled = inviteCode.length == 6
                )
            }
        }
    }
}
