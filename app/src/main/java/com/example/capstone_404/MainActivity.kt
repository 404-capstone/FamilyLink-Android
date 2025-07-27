package com.example.capstone_404

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.capstone_404.navigation.AppNavGraph
import com.example.capstone_404.social.naver.NaverAuthManager
import com.example.capstone_404.ui.theme.Capstone_404Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        //로그인 두번째 테스트 시 주석풀어서 data 초기화
//        lifecycleScope.launch {
//            NaverAuthManager.logout(this@MainActivity)
//            Log.d("App_Reset", "테스트 데이터 정리")
//        }

        // 앱 시작 시 딥링크
        handleDeepLink(intent)

        setContent {
            Capstone_404Theme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 실행 중일 때 딥링크 처리
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        Log.d("DeepLink", "딥링크 처리 시작")

        // Early return
        val uri = intent?.data ?: return logAndReturn("URI: null")

        if (!isLoginDeepLink(uri)) {
            return logAndReturn("유효하지 않음")
        }

        val sessionId = uri.getQueryParameter("session")
        if (sessionId.isNullOrBlank()) {
            return logAndReturn("세션 ID 없음")
        }

        // 세션 교환 처리
        processNaverLogin(sessionId)
    }

    private fun isLoginDeepLink(uri: Uri): Boolean {
        return uri.scheme == "familylink" && uri.host == "login"
    }

    private fun processNaverLogin(sessionId: String) {
        Log.d("DeepLink", "네이버 로그인 처리 시작")

        lifecycleScope.launch {
            try {
                val result = NaverAuthManager.loginWithSession(this@MainActivity, sessionId)
                handleLoginResult(result)
            } catch (e: Exception) {
                Log.e("DeepLink", "로그인 처리 실패: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun handleLoginResult(result: Result<String>) {
        when {
            result.isSuccess -> {
                val token = result.getOrNull()
                if (!token.isNullOrBlank()) {
                    Log.d("Login", " 로그인 성공")
                } else {
                    Log.e("Login", "token empty")
                }
            }
            else -> {
                val error = result.exceptionOrNull()?.message ?: "알 수 없는 오류"
                Log.e("Login", "로그인 실패: $error")
            }
        }
    }

    private fun logAndReturn(message: String) {
        Log.d("DeepLink", message)
    }
}