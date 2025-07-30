package com.example.capstone_404.feature.group.ui.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.group.model.GroupInfoUiState
import com.example.capstone_404.feature.group.model.GuideImageList
import com.example.capstone_404.feature.group.ui.component.GroupInfoCard
import com.example.capstone_404.feature.group.ui.component.GroupUserItem
import com.example.capstone_404.feature.group.ui.component.OnboardingGuide
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.ButtonOutline
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

// 그룹 가입(X)
@Composable
fun GroupNotJoinedContent(
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(0.5.dp, Stroke, RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "가입된 그룹이 없습니다.",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "그룹을 생성하거나 가입해보세요!",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            ButtonDefault(
                text = "생성하기",
                onClick = onCreateClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            ButtonOutline(
                text = "가입하기",
                onClick = onJoinClick,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        OnboardingGuide(images = GuideImageList.groupGuide)
    }
}

// 그룹 가입(O)
// Todo: API 연결하고 추가 수정
@Composable
fun GroupJoinedContent(
    groupInfo: GroupInfoUiState,
    currentUserId: Int,
    onEditGroup: () -> Unit,
    onInvite: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 그룹 정보 Card
        GroupInfoCard(
            groupName = groupInfo.groupName,
            groupImageUrl = groupInfo.groupImageUrl,
            isSingleUser = groupInfo.users.size == 1,
            onEditGroup = onEditGroup,
            onInvite = onInvite,
            onLeave = onLeaveGroup
        )
        // 그룹원 리스트 테두리 Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(0.5.dp, Stroke),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                     text = "그룹원",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 그룹원 리스트 Column
                Column {
                    groupInfo.users.forEach { user ->
                        GroupUserItem(
                            user = user,
                            isCurrentUser = user.userId == currentUserId
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}