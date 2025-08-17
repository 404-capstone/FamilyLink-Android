package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.diary.model.EmotionResult

@Composable
fun EmotionPieChart(
    emotions: List<EmotionResult>,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    thicknessFraction: Float = 0.4f
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawPieChart(emotions, thicknessFraction)
        }
    }
}

private fun DrawScope.drawPieChart(
    emotions: List<EmotionResult>,
    thicknessFraction: Float
) {
    val outerRadius = size.minDimension / 2
    val strokeWidth = outerRadius * thicknessFraction
    val center = Offset(size.width / 2, size.height / 2)

    // 12시 시작
    var startAngle = -90f

    emotions.forEach { emotion ->
        val sweepAngle = emotion.percentage * 360f
        val color = emotion.color
        drawArc(
            color = color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(
                center.x - outerRadius,
                center.y - outerRadius
            ),
            size = Size(outerRadius * 2, outerRadius * 2),
            style = Stroke(width = strokeWidth)
        )

        startAngle += sweepAngle
    }
}


