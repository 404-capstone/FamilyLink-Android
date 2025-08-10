package com.example.capstone_404.ui.component.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack

// 커스텀 탑바 (뒤로가기 or 취소 / 타이틀 / 삭제 or 더보기)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    navigationType: NavigationType = NavigationType.NONE,
    onNavigationClick: (() -> Unit)? = null,
    rightButton: (@Composable (() -> Unit))? = null
) {
    val navIcon: (@Composable () -> Unit)? = when (navigationType) {
        // 뒤로가기
        NavigationType.BACK -> {
            {
                IconButton(onClick = { onNavigationClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "뒤로가기",
                        modifier = Modifier.size(24.dp),
                        tint = TextBlack
                    )
                }
            }
        }
        // 닫기
        NavigationType.CLOSE -> {
            {
                IconButton(onClick = { onNavigationClick?.invoke() }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "닫기",
                        modifier = Modifier.size(24.dp),
                        tint = TextBlack
                    )
                }
            }
        }
        // 없음
        NavigationType.NONE -> null
    }

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextBlack
                )
            }
        },
        navigationIcon = navIcon ?: {},
        actions = {
            if (rightButton != null) {
                rightButton()
            } else if (navigationType != NavigationType.NONE) {
                Spacer(modifier = Modifier.width(48.dp))
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        expandedHeight = 56.dp,
        modifier = Modifier.drawBehind {
            drawLine(
                color = Stroke,
                start = Offset(0f, size.height - 0.5f),
                end   = Offset(size.width, size.height - 0.5f),
                strokeWidth = 1.dp.toPx()
            )
        }
    )
}

// 좌측 버튼 지정
enum class NavigationType {
    NONE,
    BACK,
    CLOSE
}