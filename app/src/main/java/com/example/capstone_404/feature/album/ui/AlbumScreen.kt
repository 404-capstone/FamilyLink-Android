package com.example.capstone_404.feature.album.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.album.model.toKoreanYearMonth
import com.example.capstone_404.feature.album.ui.content.AlbumScreenContent
import com.example.capstone_404.feature.album.viewmodel.AlbumViewModel
import com.example.capstone_404.ui.component.NotJoinedGroupContent
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.component.fab.SingleFab
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.utils.RequestStoragePermission
import java.net.URLEncoder

@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel = hiltViewModel(),
    onNavigateToGroup: () -> Unit,
    onNavigateToPhotoInput: (String) -> Unit,  // 이미지 URI를 파라미터로 받음
    onNavigateToPhotoDetail: (String) -> Unit = {}  // 사진 ID를 파라미터로 받음
) {
    val groupInfo by viewModel.groupInfoFlow.collectAsState(initial = null)
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val albums by viewModel.albums.collectAsState()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsState()
    val isLoading = viewModel.isLoading
    val context = LocalContext.current

    // 갈러리 권한 실행 관리 변수
    var requestGalleryPermission by remember { mutableStateOf(false) }

    // 갤러리에서 이미지 선택
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri ->
            // 선택된 이미지를 Navigation 파라미터로 전달
            val encodedUri = URLEncoder.encode(imageUri.toString(), "UTF-8")
            // 사진 정보 입력 화면으로 이동
            onNavigateToPhotoInput(encodedUri)
        }
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

    LaunchedEffect(groupInfo) {
        if (groupInfo != null && viewModel.albums.value.isEmpty()) {
            viewModel.getAllAlbums()
        }
    }

    // 로딩 dialog
    if (isLoading) {
        LoadingDialog("앨범을 불러오는 중...\n잠시만 기다려주세요!")
    }

    BackHandler(
        enabled = selectedYearMonth != null
    ) {
        viewModel.onBackPressed()
    }
    Scaffold(
        topBar = {
            when {
                selectedYearMonth != null -> {
                    CustomTopBar(
                        title = selectedYearMonth!!.toKoreanYearMonth(),
                        navigationType = NavigationType.BACK,
                        onNavigationClick = { viewModel.selectYearMonth(null) }
                    )
                }
                else -> {
                    CustomTopBar(
                        title = "앨범",
                        navigationType = NavigationType.NONE
                    )
                }
            }
        },
        containerColor = Background
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            val horizontalPadding = when {
                // 사진 그리드 상태일 때는 패딩 최소화
                selectedYearMonth != null -> 8.dp
                // 앨범 목록일 때는 기존 패딩 유지
                else -> when {
                    maxWidth < 400.dp -> 16.dp
                    maxWidth < 600.dp -> 24.dp
                    else -> 40.dp
                }
            }

            // 그룹 미가입 상태 또는 그룹 가입 상태에 따른 UI 표시
            if (groupInfo != null && userId != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AlbumScreenContent(
                        albums = albums,
                        selectedYearMonth = selectedYearMonth,
                        onYearMonthClick = viewModel::selectYearMonth,
                        onPhotoClick = onNavigateToPhotoDetail,
                        onRefresh = viewModel::getAllAlbums,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                    SingleFab(
                        onClick = {
                            requestGalleryPermission = true
                        },
                        icon = R.drawable.ic_add
                    )
                }
            } else {
                NotJoinedGroupContent(
                    onNavigateToGroup = onNavigateToGroup
                )
            }
        }
    }
}