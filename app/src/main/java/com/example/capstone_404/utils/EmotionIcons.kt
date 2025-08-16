package com.example.capstone_404.utils

import androidx.annotation.DrawableRes
import com.example.capstone_404.R
import com.example.capstone_404.feature.diary.model.EmotionType

// 감정별 아이콘 매핑 함수
@DrawableRes
fun emotionIconRes(type: EmotionType): Int = when (type) {
    EmotionType.JOY -> R.drawable.ic_smile
    EmotionType.DISGUST -> R.drawable.ic_disgust
    EmotionType.SADNESS -> R.drawable.ic_sad
    EmotionType.ANGER -> R.drawable.ic_angry
    EmotionType.HURT -> R.drawable.ic_hurt
    EmotionType.SURPRISE -> R.drawable.ic_surprise
}


