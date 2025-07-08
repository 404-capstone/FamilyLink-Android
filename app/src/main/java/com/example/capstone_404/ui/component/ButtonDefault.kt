package com.example.capstone_404.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main

// 기본 버튼
@Composable
fun ButtonDefault(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Main else ButtonDisabled,
            contentColor = Color.White
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtonPrimary() {
    ButtonDefault(
        text = "시작하기",
        onClick = {},
        enabled = true
    )
}
