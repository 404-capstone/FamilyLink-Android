package com.example.capstone_404

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.capstone_404.navigation.AppNavGraph
import com.example.capstone_404.ui.theme.Capstone_404Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)
        setContent {
            Capstone_404Theme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}