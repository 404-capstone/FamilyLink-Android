package com.example.capstone_404.feature.group.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.capstone_404.R
import com.example.capstone_404.data.info.GroupUserInfo
import com.example.capstone_404.feature.group.model.getColor
import com.example.capstone_404.feature.group.model.parseRoleAndOrder
import com.example.capstone_404.ui.component.ButtonColorRed
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun GroupMemberDialog(
    user: GroupUserInfo,
    currentUserId: Int,
    groupLeaderId: Int,
    onDismiss: () -> Unit,
    onTransferLeader: () -> Unit,
    onExpel: () -> Unit
) {
    val splitRole = parseRoleAndOrder(user.role)
    val isSelectedUserLeader = user.leader
    val isMyProfile = currentUserId == user.userId
    val iAmLeader = currentUserId == groupLeaderId
    // 넓이 조절
    val surfaceModifier = if (iAmLeader && !isMyProfile) {
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    } else {
        Modifier
            .wrapContentHeight()
            .width(250.dp)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = surfaceModifier,
            shape = RoundedCornerShape(16.dp),
            color = Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // 닫기 버튼
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "닫기",
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.CenterEnd)
                            .clickable { onDismiss() },
                        tint = Main
                    )
                }
                // 프로필 이미지
                AsyncImage(
                    model = user.image,
                    contentDescription = "그룹원 이미지",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(Stroke)
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 이름 + 연령 Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isMyProfile) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Stroke),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "나",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextWhite,
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.age,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextBlack
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                // 색상 + 역할 Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(splitRole.first.getColor(splitRole.second))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.role,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBlack
                    )
                }
                // 그룹장은 그룹장 표시
                if (isSelectedUserLeader) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "[그룹장]",
                        color = Main,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                // 그룹장이 그룹원 정보를 열었을 때만
                if (iAmLeader && !isMyProfile) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ButtonColorRed(
                            text = "그룹에서 추방",
                            onClick = onExpel,
                            modifier = Modifier.weight(1f)
                        )
                        ButtonDefault(
                            text = "그룹장 이전",
                            onClick = onTransferLeader,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}