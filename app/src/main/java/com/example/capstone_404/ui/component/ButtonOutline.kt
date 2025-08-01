package com.example.capstone_404.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

// 아웃 라인 버튼
@Composable
fun ButtonOutline(
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onClick: () -> Unit,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) Color.White else ButtonDisabled,
            contentColor = if (enabled) Main else Stroke
        ),
        border = BorderStroke(1.dp, if (enabled) Main else Stroke),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = style
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtonOutline() {
    ButtonOutline(
        text = "가입하기",
        onClick = {},
        enabled = true
    )
}
