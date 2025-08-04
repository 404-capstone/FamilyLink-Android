package com.example.capstone_404.feature.group.ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack

// 사진 선택 옵션 다이얼로그
@Composable
fun ImageSelectDialog(
    onSelectFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onUseBeforeImage: () -> Unit,
    onUseDefaultImage: () -> Unit,
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
                Text(
                    text = "사진 변경",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )

                Spacer(modifier = Modifier.height(16.dp))

                OptionItem(
                    icon = R.drawable.ic_gallery,
                    text = "라이브러리에서 선택",
                    onClick = onSelectFromGallery
                )

                OptionItem(
                    icon = R.drawable.ic_camera,
                    text = "사진 찍기",
                    onClick = onTakePhoto
                )

                OptionItem(
                    icon = R.drawable.ic_before,
                    text = "이전 이미지 사용",
                    onClick = onUseBeforeImage
                )

                OptionItem(
                    icon = R.drawable.ic_delete,
                    text = "기본 이미지 사용",
                    onClick = onUseDefaultImage
                )
            }
        }
    }
}

// 옵션 UI
@Composable
fun OptionItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = TextBlack
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextBlack
        )
    }
}