package com.example.capstone_404.ui.component

import androidx.annotation.DrawableRes
import com.example.capstone_404.R
import com.example.capstone_404.navigation.Route

// 바텀바 탭 정의
data class BottomTab(
    val route: String,
    val label: String,
    @DrawableRes val iconResId: Int
)

val bottomTabs = listOf(
    BottomTab(Route.GROUP, "그룹", R.drawable.ic_group),
    BottomTab(Route.CALENDAR, "캘린더", R.drawable.ic_calendar),
    BottomTab(Route.DIARY, "다이어리", R.drawable.ic_diary),
    BottomTab(Route.ALBUM, "앨범", R.drawable.ic_album),
    BottomTab(Route.MYPAGE, "내정보", R.drawable.ic_mypage)
)