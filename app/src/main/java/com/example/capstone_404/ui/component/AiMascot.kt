package com.example.capstone_404.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R

// 활동 추천 생성&피드백 생성 로딩에 출력
@Composable
fun AiMascot(
) {
    val transition = rememberInfiniteTransition(label = "mascot")
    // 크기 변화 애니메이션
    val scale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    // 위아래 이동 애니메이션
    val bounce by transition.animateFloat(
        initialValue = -6f,
        targetValue =  6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Image(
        painter = painterResource(R.drawable.ai_robot),
        contentDescription = null,
        modifier = Modifier
            .size(420.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = bounce
            }
    )
}