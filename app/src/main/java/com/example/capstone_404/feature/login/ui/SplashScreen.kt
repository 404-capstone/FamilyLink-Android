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

// 자동 로그인 처리 화면
@Composable
fun SplashScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoggedIn: () -> Unit,
    onNotLoggedIn: () -> Unit
    ) {
    // 자동 로그인 상태 변수
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAutoLogin()
    }

    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> onLoggedIn()
            false -> onNotLoggedIn()
            else -> onNotLoggedIn()
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
        CircularProgressIndicator()
    }
}