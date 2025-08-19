package com.example.capstone_404.feature.calendar.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.ui.component.scheduledetail.CommentInputBar
import com.example.capstone_404.feature.calendar.ui.content.ScheduleDetailContent
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun ScheduleDetailScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    scheduleId: Int,
    scheduleTitle: String,
    writerId: Int?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.scheduleDetail.collectAsState()
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val isSaveLoading = viewModel.isSaveLoading

    // 더보기 출력 조건
    val canShowMore = remember(uiState.data, writerId, userId) {
        val isWriter = writerId != null && userId != null && writerId == userId
        val isParticipant =
            userId != null && (uiState.data?.participantIds?.contains(userId) == true)
        isWriter || isParticipant
    }
    // 다이얼로그 출력 여부
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 댓글 입력 상태
    var commentText by rememberSaveable { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }



    LaunchedEffect(scheduleId) { viewModel.getScheduleDetail(scheduleId) }

    if (isSaveLoading) {
        LoadingDialog("참여 정보를 변경하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "일정 상세",
                navigationType = NavigationType.BACK,
                onNavigationClick = onBack,
                rightButton = if (canShowMore) {
                    {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_more),
                                    contentDescription = null,
                                    tint = TextBlack
                                )
                            }
                        }
                    }
                } else null
            )
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

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Error,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                uiState.data != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(
                                    horizontal = horizontalPadding,
                                    vertical = verticalPadding
                                )
                                .padding(bottom = 104.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 상세 정보
                            ScheduleDetailContent(
                                data = uiState.data!!,
                                writerId = writerId,
                                title = scheduleTitle,
                                userId = userId,
                                userIdToRole = userIdToRole,
                                onToggleJoin = { join ->
                                    viewModel.toggleScheduleParticipation(
                                        scheduleId = scheduleId,
                                        userId = userId!!,
                                        join = join,
                                        onError = { e ->
                                            Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            )
                        }
                        // 댓글 입력 바
                        CommentInputBar(
                            value = commentText,
                            onValueChange = { commentText = it },
                            isSending = isSending,
                            onSend = {
                                if (commentText.isNotBlank()) {
                                    isSending = true
                                    // Todo : 댓글 추가 API 연동 후 수정
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = horizontalPadding, vertical = 12.dp)
                                .imePadding()
                                .navigationBarsPadding()
                        )
                    }
                }
            }
        }
    }
}