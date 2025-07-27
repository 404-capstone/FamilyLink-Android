package com.example.capstone_404

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.capstone_404.navigation.AppNavGraph
import com.example.capstone_404.ui.theme.Capstone_404Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingSessionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)
        handleDeepLink(intent) // 앱 시작 시 딥링크
        setContent {
            Capstone_404Theme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent) // 실행 중일 때 딥링크 처리
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data
        if (uri != null && uri.scheme == "familylink" && uri.host == "login") {
            val sessionId = uri.getQueryParameter("session")
            if (sessionId != null) {
                pendingSessionId = sessionId
                Log.d("DeepLink", "Received session ID: $sessionId")
            }
        }
    }
}