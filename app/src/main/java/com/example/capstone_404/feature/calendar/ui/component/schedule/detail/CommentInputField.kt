package com.example.capstone_404.feature.calendar.ui.component.schedule.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    isSending: Boolean,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canInput = !isSending
    val canSend = canInput && value.isNotBlank()

    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    // 라인 한줄 높이
    val lineHeight = with(density) { MaterialTheme.typography.bodyMedium.lineHeight.toDp() }
    // 출력 최대 5줄
    val maxVisibleHeight = lineHeight * 5 + 16.dp

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (canSend) Main else Stroke),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 40.dp, max = maxVisibleHeight)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 댓글 입력 필드
            TextField(
                value = value,
                onValueChange = { onValueChange(it.take(100)) },
                enabled = canInput,
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = {
                    Text(
                        text = "댓글 입력",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Main.copy(alpha = 0.6f)
                    )
                },
                singleLine = false,
                minLines = 1,
                maxLines = 10,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = TextBlack,
                    focusedTextColor = TextBlack,
                    unfocusedTextColor = TextBlack
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            // 전송 버튼
            IconButton(
                enabled = canSend,
                onClick = {
                    if (canSend) {
                        onSend()
                        keyboard?.hide()
                        focusManager.clearFocus()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_enter),
                    contentDescription = "전송",
                    tint = if (canSend) Main else TextGray
                )
            }
        }
    }
}