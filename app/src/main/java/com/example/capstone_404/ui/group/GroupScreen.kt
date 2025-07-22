package com.example.capstone_404.ui.group

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.group.screen.GroupJoinedContent
import com.example.capstone_404.ui.group.screen.GroupNotJoinedContent
import com.example.capstone_404.ui.group.viewmodel.GroupViewModel

@Composable
fun GroupScreen(
    viewModel: GroupViewModel = hiltViewModel()
) {
    // 그룹 가입 여부
    val isJoined = viewModel.isJoined

    Scaffold(
        topBar = {
            CustomTopBar(title = "그룹")
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 24.dp
                maxWidth < 600.dp -> 32.dp
                else -> 40.dp
            }

            val verticalPadding = when {
                maxHeight < 600.dp -> 24.dp
                maxHeight < 800.dp -> 32.dp
                else -> 40.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isJoined) {
                    GroupJoinedContent()
                } else {
                    GroupNotJoinedContent(
                        onCreateClick = {},
                        onJoinClick = {}
                    )
                }
            }
        }
    }
}