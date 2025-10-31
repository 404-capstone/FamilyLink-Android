package com.example.capstone_404

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.capstone_404.feature.login.viewmodel.LoginViewModel
import com.example.capstone_404.navigation.AppNavGraph
import com.example.capstone_404.ui.theme.Capstone_404Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()
    private var navControllerRef: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)

        setContent {
            Capstone_404Theme {
                val navController = rememberNavController()

                // NavController를 Activity에서도 쓸 수 있게 저장
                DisposableEffect(Unit) {
                    navControllerRef = navController
                    onDispose { navControllerRef = null }
                }

                // 앱 시작 시 딥링크 처리
                LaunchedEffect(Unit) {
                    navController.handleDeepLink(intent)
                }

                AppNavGraph(navController)
            }
        }
    }

    // 앱 실행 중 외부에서 딥링크 들어올 때 처리
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
        navControllerRef?.handleDeepLink(intent)
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