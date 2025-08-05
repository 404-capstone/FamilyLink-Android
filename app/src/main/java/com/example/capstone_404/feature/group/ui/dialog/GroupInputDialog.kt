package com.example.capstone_404.feature.group.ui.dialog

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.capstone_404.R
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun GroupInputDialog(
    isEdit: Boolean = false,
    initialGroupName: String = "",
    initialGroupImage: String? = null,
    selectedImageUri: Uri?,
    onDismiss: () -> Unit,
    onSelectPhoto: () -> Unit,
    onConfirm: (groupName: String, imageUri: Uri?) -> Unit,
) {
    // 그룹 이름 최대 길이
    val maxLength = 20
    var groupName by remember { mutableStateOf(initialGroupName) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
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
                // 대표 사진
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .clickable { onSelectPhoto() }
                        .background(Stroke),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri == null) {
                        if (initialGroupImage != null) {
                            AsyncImage(
                                model = initialGroupImage,
                                contentDescription = "기존 그룹 이미지",
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.default_group),
                                contentDescription = "기본 그룹 이미지",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    } else {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "선택한 그룹 이미지",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // 텍스트 버튼
                Text(
                    text = "대표 사진 변경",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Main,
                    modifier = Modifier.clickable { onSelectPhoto() }
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 그룹명 입력 필드
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { if (it.length <= maxLength) groupName = it },
                    placeholder = { Text("그룹 이름") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Main,
                        unfocusedBorderColor = Stroke,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = TextBlack,
                        focusedPlaceholderColor = TextGray,
                        unfocusedPlaceholderColor = TextGray
                    )
                )
                // 적힌 글자 수 표시
                Text(
                    text = "${groupName.length} / $maxLength",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(10.dp))
                // 버튼 Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ButtonOutline(
                        text = "취소",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    ButtonDefault(
                        text = if (isEdit) "정보 수정" else "그룹 생성",
                        onClick = {
                            onConfirm(groupName, selectedImageUri)
                        },
                        enabled = groupName.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}