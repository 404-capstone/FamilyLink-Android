package com.example.capstone_404.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.capstone_404.R

// Pretendard FontFamily
val Pretendard = FontFamily(
    Font(resId = R.font.pretendard_bold, weight = FontWeight.Bold),
    Font(resId = R.font.pretendard_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.pretendard_medium, weight = FontWeight.Medium),
    Font(resId = R.font.pretendard_regular, weight = FontWeight.Normal),
)

// Typography 설정
val Typography = Typography(
    // Headline 1
    headlineLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 26.4.sp
    ),
    // Headline 2
    headlineMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 22.sp
    ),
    // Headline 3
    headlineSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 19.2.sp
    ),

    // Body Bold
    bodyLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body Medium
    bodyMedium = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body Regular 14p
    bodySmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.6.sp
    ),
    // Body Regular 12p
    labelSmall = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.4.sp
    )
)