package com.example.capstone_404.ui.component.fab

data class ExpandableFabItem(
    val text: String,
    val iconResId: Int,
    val onClick: () -> Unit
)