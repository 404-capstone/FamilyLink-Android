package com.example.capstone_404

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.capstone_404.feature.login.viewmodel.LoginViewModel
import com.example.capstone_404.navigation.AppNavGraph
import com.example.capstone_404.ui.theme.Capstone_404Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        // 앱 실행 시 딥링크 확인
        handleDeepLink(intent)

        setContent {
            Capstone_404Theme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }

    // 앱 실행 중 외부에서 딥링크 들어올 때 처리
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data
        if (uri != null) {
            Log.d("DeepLink", "딥링크 처리 시작")
            Log.d("DeepLink", "URI 정상 수신: $uri")

            val sessionId = uri.getQueryParameter("session")

            if (!sessionId.isNullOrBlank()) {
                Log.d("DeepLink", "SessionId: $sessionId")
                viewModel.saveSessionId(sessionId)
            } else {
                Log.e("DeepLink", "SessionId 없음")
            }
        }
    }
}