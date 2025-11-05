package com.example.capstone_404.feature.album.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.album.model.Photo
import com.example.capstone_404.feature.album.ui.component.AlbumInputField
import com.example.capstone_404.feature.album.ui.component.DateTimePickerField
import com.example.capstone_404.feature.album.ui.component.LocationField
import com.example.capstone_404.feature.album.ui.component.ParticipantSelector
import com.example.capstone_404.feature.album.viewmodel.AlbumViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.DetailBg
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun PhotoEditScreen(
    viewModel: AlbumViewModel,
    photo: Photo,
    onNavigateBack: () -> Unit
) {
    val photoEditState by viewModel.photoEditState.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val isUpdating = viewModel.isUpdating
    val updateError = viewModel.updateError
    val updateSuccess = viewModel.updateSuccess
    val context = LocalContext.current

    var showExitDialog by remember { mutableStateOf(false) }

    // 변경사항 감지
    val hasChanges by remember {
        derivedStateOf {
            viewModel.hasChanges(photo)
        }
    }

    // 초기 데이터 설정
    LaunchedEffect(photo) {
        viewModel.initializeEditState(photo)
    }

    // 수정 성공 시 처리
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            if (hasChanges) {
                Toast.makeText(context, "사진 정보가 수정되었습니다", Toast.LENGTH_SHORT).show()
            }
            viewModel.resetPhotoEditState()
            onNavigateBack()
        }
    }

    // 수정 에러 처리
    LaunchedEffect(updateError) {
        updateError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearUpdateError()
        }
    }

    BackHandler {
        if (hasChanges) {
            showExitDialog = true
        } else {
            onNavigateBack()
        }
    }

    if (isUpdating) {
        LoadingDialog("사진 정보를 수정하는 중...")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "사진 정보 수정",
                navigationType = NavigationType.BACK,
                onNavigationClick = {
                    if (hasChanges) {
                        showExitDialog = true
                    } else {
                        onNavigateBack()
                    }
                },
                rightButton = {
                    IconButton(
                        onClick = {
                            when {
                                photoEditState.title.isEmpty() -> {
                                    Toast.makeText(context, "사진 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                photoEditState.date.isEmpty() -> {
                                    Toast.makeText(context, "촬영 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    viewModel.updatePhoto(photo.id)
                                }
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            contentDescription = "수정 완료",
                            tint = TextBlack
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DetailBg)
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
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 입력
                Column {
                    AlbumInputField(
                        value = photoEditState.title,
                        onValueChange = viewModel::updateEditTitle,
                        placeholder = "제목을 입력하세요.",
                        maxLength = 50
                    )
                    // 제목 글자 수
                    Text(
                        text = "${photoEditState.title.length} / 50",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                // 날짜/시간 선택
                DateTimePickerField(
                    date = photoEditState.date,
                    time = photoEditState.time,
                    onDateSelected = viewModel::updateEditDate,
                    onTimeSelected = viewModel::updateEditTime
                )

                // 장소 입력
                LocationField(
                    location = photoEditState.location,
                    onLocationChanged = viewModel::updateEditLocation
                )

                // 메모 입력
                AlbumInputField(
                    value = photoEditState.description,
                    onValueChange = viewModel::updateEditDescription,
                    placeholder = "메모 (선택 사항)",
                    leadingIcon = painterResource(R.drawable.ic_help),
                    maxLength = 250,
                    minLines = 1,
                    maxLines = 8
                )

                // 참여자 선택
                ParticipantSelector(
                    members = groupMembers,
                    selectedParticipants = photoEditState.selectedParticipants.toSet(),
                    onParticipantToggle = { userId ->
                        val current = photoEditState.selectedParticipants.toSet()
                        val updated = if (current.contains(userId)) {
                            current - userId
                        } else {
                            current + userId
                        }
                        viewModel.updateEditParticipants(updated)
                    },
                    onSetSelectedParticipants = viewModel::updateEditParticipants
                )

                // 에러 메시지 표시
                updateError?.let { error ->
                    Text(
                        text = error,
                        color = Error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    // 변경사항 확인 다이얼로그
    if (showExitDialog) {
        ActionDialog(
            title = "수정을 취소하시겠습니까?",
            description = "변경된 내용이 저장되지 않습니다.",
            confirmText = "취소",
            onConfirm = {
                showExitDialog = false
                onNavigateBack()
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }
}