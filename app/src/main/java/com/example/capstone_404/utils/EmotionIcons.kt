package com.example.capstone_404.utils

import androidx.annotation.DrawableRes
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.EmotionType

// 감정별 아이콘 매핑 함수
@DrawableRes
fun emotionIconRes(type: EmotionType): Int = when (type) {
    EmotionType.HAPPINESS -> R.drawable.ic_happiness
    EmotionType.DISGUST -> R.drawable.ic_disgust
    EmotionType.SADNESS -> R.drawable.ic_sad
    EmotionType.ANGER -> R.drawable.ic_angry
    EmotionType.ANXIETY -> R.drawable.ic_anxiety
    EmotionType.SURPRISE -> R.drawable.ic_surprise
}


