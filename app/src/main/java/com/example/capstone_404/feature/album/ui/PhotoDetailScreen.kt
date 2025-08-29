package com.example.capstone_404.feature.album.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import com.example.capstone_404.feature.album.model.DateTimeUtil
import com.example.capstone_404.feature.album.ui.component.PhotoDetailBottomSheet
import com.example.capstone_404.feature.album.viewmodel.AlbumViewModel
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Error
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    viewModel: AlbumViewModel,
    photoId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {}
) {
    val albums by viewModel.albums.collectAsState()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val isDeletingPhoto = viewModel.isDeletingPhoto
    val deleteError = viewModel.deleteError
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 현재 앨범의 모든 사진 목록 (최신순 정렬)
    val photos = remember(albums, selectedYearMonth) {
        selectedYearMonth?.let { yearMonth ->
            albums[yearMonth]?.sortedByDescending { it.sortableDateTime } ?: emptyList()
        } ?: emptyList()
    }

    // 초기 페이지 인덱스 (선택된 사진 ID 기준)
    val initialPage = remember(photos, photoId) {
        photos.indexOfFirst { it.id == photoId }.takeIf { it >= 0 } ?: 0
    }

    // HorizontalPager 상태
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { photos.size }
    )

    // 현재 사진
    val currentPhoto by remember {
        derivedStateOf {
            photos.getOrNull(pagerState.currentPage)
        }
    }

    // 현재 사진의 날짜/시간 포맷
    val currentPhotoDateTime by remember {
        derivedStateOf {
            currentPhoto?.let { photo ->
                DateTimeUtil.formatDateTime(photo.date, photo.time)
            } ?: "사진 정보"
        }
    }

    // BottomSheet 상태
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    // 삭제 에러 처리
    LaunchedEffect(deleteError) {
        deleteError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteError()
        }
    }

    if (isDeletingPhoto) {
        LoadingDialog("사진을 삭제하는 중...")
    }


    // 갤러리가 비어있을 때 처리
    if (photos.isEmpty()) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = "사진을 찾을 수 없음",
                    navigationType = NavigationType.BACK,
                    onNavigationClick = onNavigateBack
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "표시할 사진이 없습니다.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextBlack
                )
            }
        }
        return
    }

    // 메인 화면
    Scaffold(
        topBar = {
            CustomTopBar(
                title = currentPhotoDateTime,
                navigationType = NavigationType.BACK,
                onNavigationClick = onNavigateBack,
                rightButton = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more),
                                contentDescription = "더보기",
                                tint = TextBlack
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 0.dp,
                                bottomStart = 12.dp, bottomEnd = 12.dp),
                            tonalElevation = 0.dp,
                            shadowElevation = 8.dp
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_edit),
                                        contentDescription = null,
                                        tint = TextBlack
                                    )
                                },
                                text = {
                                    Text(
                                        text = "수정",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextBlack
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    currentPhoto?.let { photo ->
                                        onNavigateToEdit(photo.id)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_withdraw),
                                        contentDescription = null,
                                        tint = Error
                                    )
                                },
                                text = {
                                    Text(
                                        text = "삭제",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val screenHeight = maxHeight
            val peekHeight = screenHeight * 0.3f

            BottomSheetScaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = scaffoldState,
                sheetPeekHeight = peekHeight,
                sheetSwipeEnabled = true,
                sheetDragHandle = {
                    BottomSheetDefaults.DragHandle(color = Stroke)
                },
                sheetContainerColor = Color.White,
                containerColor = Background,
                sheetContent = {
                    currentPhoto?.let { photo ->
                        PhotoDetailBottomSheet(
                            photo = photo,
                            groupMembers = groupMembers
                        )
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "사진 정보를 불러오는 중...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextBlack
                            )
                        }
                    }
                }
            ) { _ ->
                BoxWithConstraints {
                    val screenHeight = maxHeight
                    val photoAreaHeight = screenHeight * 0.7f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(photoAreaHeight)
                            .background(Background),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val photo = photos[page]

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(photo.imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = photo.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (showDeleteDialog) {
        ActionDialog(
            title = "사진을 삭제하시겠습니까?",
            description = "삭제된 사진은 복구할 수 없습니다.",
            confirmText = "삭제",
            onConfirm = {
                showDeleteDialog = false
                currentPhoto?.let { photo ->
                    viewModel.deletePhoto(photo.id) {
                        onNavigateBack()
                    }
                }
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}