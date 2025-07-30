package com.example.capstone_404.feature.group.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

// 그룹 화면 내에 설문 Fab
@Composable
fun SurveyFab(
    expanded: Boolean,
    onToggle: () -> Unit,
    onSurveyWriteClick: () -> Unit,
    onSurveyResultClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            AnimatedVisibility(visible = expanded) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    CustomExtendFab(text = "결과 조회", icon = R.drawable.ic_surveyresult, onClick = onSurveyResultClick)
                    CustomExtendFab(text = "설문 작성", icon = R.drawable.ic_surveywrite, onClick = onSurveyWriteClick)
                }
            }

            FloatingActionButton(
                onClick = onToggle,
                containerColor = Main,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp ,start = 16.dp, end = 16.dp)
                    .size(56.dp),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.ic_delete else R.drawable.ic_survey),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

// 확장 Fab
@Composable
fun CustomExtendFab(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Main
            ) },
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Main
            ) },
        onClick = onClick,
        containerColor = Color.White,
        contentColor = Main,
        shape = RoundedCornerShape(50),
        elevation = FloatingActionButtonDefaults.elevation(4.dp),
        modifier = Modifier.border(0.5.dp, Stroke, RoundedCornerShape(50))
    )
}