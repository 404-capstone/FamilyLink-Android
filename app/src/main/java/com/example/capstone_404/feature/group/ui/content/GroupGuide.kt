package com.example.capstone_404.feature.group.ui.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.ui.theme.ButtonDisabled
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

@Composable
fun GroupGuide() {
    val guideImages = listOf(
        // Todo: 현재 샘플 이미지, UI 구현 후 수정
        R.drawable.guide_group,
        R.drawable.guide_calendar,
        R.drawable.guide_diary,
        R.drawable.guide_album
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { guideImages.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(0.5.dp, Stroke, RoundedCornerShape(12.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { page ->
            Image(
                painter = painterResource(id = guideImages[page]),
                contentDescription = "가이드 $page",
                modifier = Modifier.fillMaxSize()
            )
        }
        // 인디케이터 Row
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            repeat(guideImages.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .background(
                            color = if (selected) Main else ButtonDisabled,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}