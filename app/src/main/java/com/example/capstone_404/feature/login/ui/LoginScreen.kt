package com.example.capstone_404.feature.login.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.login.ui.componet.KakaoLoginButton
import com.example.capstone_404.feature.login.ui.componet.NaverLoginButton
import com.example.capstone_404.feature.login.viewmodel.LoginViewModel
import com.example.capstone_404.social.naver.NaverAuthManager
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    // 로그인 결과 변수
    val loginResult = viewModel.socialLoginState

    // 네이버 로그인 상태 모니터링
    val isNaverLoggedIn by NaverAuthManager.getLoginState(context).collectAsState(initial = false)

    // 네이버 로그인 결과 처리
    LaunchedEffect(isNaverLoggedIn) {
        if (isNaverLoggedIn) {
            val token = NaverAuthManager.getToken(context)
            if (!token.isNullOrEmpty()) {
                Log.d("LoginScreen", "네이버 로그인 상태 감지")
                onLoginSuccess(token)
            }
        }
    }

    // 로그인 결과 처리
    LaunchedEffect(loginResult) {
        loginResult?.onSuccess { token ->
            onLoginSuccess(token)
        }?.onFailure { e ->
            Toast.makeText(context, "로그인 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
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

        // 배경 이미지
        Image(
            painter = painterResource(R.drawable.login_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 전체 컬럼
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 로고 텍스트
            Text(
                text = "Family Link",
                fontSize = 48.sp,
                fontFamily = FontFamily(Font(R.font.playpensans_regular)),
                color = TextWhite
            )

            // 로그인 버튼 컬럼
            Column {
                KakaoLoginButton(onClick = { viewModel.loginWithKakao(context) })

                Spacer(modifier = Modifier.height(16.dp))

                NaverLoginButton(onClick = { viewModel.loginWithNaver(context) })
            }
        }
    }
}