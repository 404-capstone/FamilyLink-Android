package com.example.capstone_404.feature.group.model

import com.example.capstone_404.R

// 그룹 화면 기본 프로필 사진
fun getDefaultImage(role: String): Int {
    return when {
        role.contains("할아버지") -> R.drawable.default_grandpa
        role.contains("할머니") -> R.drawable.default_grandma
        role.contains("아빠") -> R.drawable.default_dad
        role.contains("엄마") -> R.drawable.default_mom
        role.contains("아들") -> R.drawable.default_son
        role.contains("딸") -> R.drawable.default_daughter
        else -> R.drawable.default_profile
    }
}