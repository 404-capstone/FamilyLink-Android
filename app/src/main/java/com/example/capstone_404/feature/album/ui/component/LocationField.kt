package com.example.capstone_404.feature.album.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.capstone_404.R

@Composable
fun LocationField(
    location: String,
    onLocationChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AlbumInputField(
        value = location,
        onValueChange = onLocationChanged,
        placeholder = "장소 (선택 사항)",
        leadingIcon = painterResource(R.drawable.ic_location),
        maxLength = 50,
        modifier = modifier
    )
}