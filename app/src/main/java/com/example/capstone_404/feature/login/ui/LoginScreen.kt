package com.example.capstone_404.feature.login.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.capstone_404.feature.login.model.LoginState
import com.example.capstone_404.feature.login.ui.componet.KakaoLoginButton
import com.example.capstone_404.feature.login.ui.componet.NaverLoginButton
import com.example.capstone_404.feature.login.viewmodel.LoginViewModel
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.TextWhite
import com.example.capstone_404.utils.SocialLoginManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToProfileInput: () -> Unit
) {
    val context = LocalContext.current
    // 로그인 상태 변수
    val loginState by viewModel.loginState.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    // 추가 정보 입력 분기용
    val userGender by viewModel.userGenderFlow.collectAsState(initial = null)
    val userAge by viewModel.userAgeFlow.collectAsState(initial = null)

    // SessionId 감지 시 로그인 처리
    LaunchedEffect(Unit) {
        val sessionId = withContext(Dispatchers.IO) {
            viewModel.tokenManager.getSessionId()
        }
        if (sessionId.isNotBlank()) {
            viewModel.loginWithSessionId(sessionId)
        }
    }

    // 로그인 상태에 따른 처리
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                viewModel.saveUserInfo()
            }
            is LoginState.Error -> {
                val errorMsg = (loginState as LoginState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    // 유저 정보 저장 완료 시 이동
    LaunchedEffect(isSaved) {
        if (isSaved == true && (userGender == null || userAge == null)) {
            viewModel.resetSaved()
            onNavigateToProfileInput()
        } else if (isSaved == true) {
            viewModel.resetSaved()
            onNavigateToHome()
        }
    }

    // 로그인 진행 중 로딩 표시
    if (loginState is LoginState.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Main)
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
                KakaoLoginButton(onClick = { SocialLoginManager.login(context, SocialLoginManager.SocialPlatform.KAKAO) })

                Spacer(modifier = Modifier.height(16.dp))

                NaverLoginButton(onClick = { SocialLoginManager.login(context, SocialLoginManager.SocialPlatform.NAVER) })
            }
        }
    }
}