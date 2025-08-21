package com.example.capstone_404.feature.album.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@Composable
fun AlbumScreen(
    viewModel: AlbumViewModel = hiltViewModel(),
    onNavigateToGroup: () -> Unit
) {
    val groupInfo by viewModel.groupInfoFlow.collectAsState(initial = null)
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val albums by viewModel.albums.collectAsState()
    val selectedYearMonth by viewModel.selectedYearMonth.collectAsState()
    val isLoading = viewModel.isLoading

    // 그룹 정보가 있을 때만 앨범 데이터 로드
    LaunchedEffect(groupInfo) {
        if (groupInfo != null) viewModel.getAllAlbums()
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
            // 반응형 패딩
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
                        onRefresh = viewModel::getAllAlbums,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                    //사진추가
                    SingleFab(
                        onClick = { },
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
