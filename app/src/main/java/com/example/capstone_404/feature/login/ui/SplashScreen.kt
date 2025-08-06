package com.example.capstone_404.feature.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.login.viewmodel.LoginViewModel
import com.example.capstone_404.ui.theme.Main

// 자동 로그인 처리 화면
@Composable
fun SplashScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit,
    onNotLoggedIn: () -> Unit
    ) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()

    // 로그인 상태 확인
    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    // 자동 로그인 성공 후 그룹 ID 저장
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn == true) {
            viewModel.saveUserInfo()
        } else if (isLoggedIn == false) {
            onNotLoggedIn()
        }
    }

    // 그룹 ID & 그룹 정보 저장 완료 시 이동
    LaunchedEffect(isSaved) {
        if (isSaved) {
            onLoggedIn()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // 배경 이미지
        Image(
            painter = painterResource(R.drawable.login_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        // 로딩 UI
        CircularProgressIndicator(color = Main)
    }
}