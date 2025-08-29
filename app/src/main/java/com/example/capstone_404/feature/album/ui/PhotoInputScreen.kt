package com.example.capstone_404.feature.album.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.capstone_404.R
import com.example.capstone_404.feature.album.ui.component.AlbumInputField
import com.example.capstone_404.feature.album.ui.component.DateTimePickerField
import com.example.capstone_404.feature.album.ui.component.LocationField
import com.example.capstone_404.feature.album.ui.component.ParticipantSelector
import com.example.capstone_404.feature.album.viewmodel.AlbumViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import com.example.capstone_404.utils.RequestStoragePermission

@Composable
fun PhotoInputScreen(
    viewModel: AlbumViewModel,
    initialImageUri: Uri,
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val photoAddState by viewModel.photoAddState.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // 갤러리 권한 실행 관리 변수
    var requestGalleryPermission by remember { mutableStateOf(false) }

    // 갤러리 실행
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    // 초기 이미지 URI 설정
    LaunchedEffect(initialImageUri) {
        viewModel.setSelectedImage(initialImageUri)
    }

    // 에러 메시지 토스트 처리
    LaunchedEffect(photoAddState.errorMessage) {
        photoAddState.errorMessage?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearPhotoAddError()
        }
    }

    // 뒤로가기 처리
    BackHandler {
        onNavigateBack()
    }

    // 저장소 권한 요청 실행
    if (requestGalleryPermission) {
        RequestStoragePermission(
            context = context,
            onGranted = {
                galleryLauncher.launch("image/*")
                requestGalleryPermission = false
            },
            onDenied = {
                requestGalleryPermission = false
            }
        )
    }

    // 로딩 다이얼로그
    if (photoAddState.isLoading) {
        LoadingDialog("사진 정보를 저장하는 중...\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "상세 정보",
                navigationType = NavigationType.BACK,
                onNavigationClick = onNavigateBack,
                rightButton = {
                    IconButton(
                        onClick = {
                            // 필수 입력 사항 검증
                            when {
                                photoAddState.selectedImage == null -> {
                                    Toast.makeText(context, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                photoAddState.title.isEmpty() -> {
                                    Toast.makeText(context, "사진 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                photoAddState.date.isEmpty() -> {
                                    Toast.makeText(context, "촬영 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    viewModel.addPhoto(
                                        onSuccess = {
                                            Toast.makeText(context, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show()
                                            onSaveComplete()
                                        }
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "완료",
                            tint = TextBlack
                        )
                    }
                }
            )
        },
        containerColor = Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            photoAddState.selectedImage?.let { imageUri ->
                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f),  // 4:3 비율
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "선택된 사진",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.ic_info)
                        )
                    }

                    // 사진 변경 버튼
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(44.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                            .border(
                                width = 0.5.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // 갤러리 권한 요청 후 실행
                                requestGalleryPermission = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_album),
                            contentDescription = "사진 변경",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } ?: run {
                // 이미지가 없을 때 임시 표시
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "선택된 이미지가 없습니다",
                            color = TextGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 제목 입력 (필수)
                Column {
                    AlbumInputField(
                        value = photoAddState.title,
                        onValueChange = viewModel::updateTitle,
                        placeholder = "제목을 입력하세요 *",
                        maxLength = 50
                    )
                    // 제목 글자 수
                    Text(
                        text = "${photoAddState.title.length} / 50",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
                // 날짜/시간 선택
                DateTimePickerField(
                    date = photoAddState.date,
                    time = photoAddState.time,
                    onDateSelected = viewModel::updateDate,
                    onTimeSelected = viewModel::updateTime
                )
                // 장소 입력
                LocationField(
                    location = photoAddState.location,
                    onLocationChanged = viewModel::updateLocation
                )
                // 설명 입력
                AlbumInputField(
                    value = photoAddState.description,
                    onValueChange = viewModel::updateDescription,
                    placeholder = "설명 (선택 사항)",
                    maxLength = 250,
                    minLines = 5,
                    maxLines = 8
                )
                // 참여자 선택
                ParticipantSelector(
                    members = groupMembers,  // 그룹 멤버 정보
                    selectedParticipants = photoAddState.selectedParticipants.toSet(),
                    onParticipantToggle = viewModel::toggleParticipant,
                    onSetSelectedParticipants = viewModel::updateSelectedParticipants
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}